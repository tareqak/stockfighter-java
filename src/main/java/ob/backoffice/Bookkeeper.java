package ob.backoffice;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ob.abstractions.*;
import ob.backoffice.abstractions.*;
import ob.backoffice.websocket.abstractions.Execution;
import ob.requests.NewOrderRequest;
import ob.responses.NewOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.Utilities;

import java.io.Closeable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bookkeeper implements Closeable {
    private static final Logger logger =
            LoggerFactory.getLogger(Bookkeeper.class);

    private final BlockingQueue<Execution> executionBlockingQueue;
    private final Map<Stocks.Stock, Map<String, PositionStatus>>
            stockAccountPositionMap = new HashMap<>(10);
    private final Cache<Integer, OrderStatus> orderStatusCache;

    private final CashStatus cashStatus;
    private final List<Thread> workers;
    private final AtomicBoolean done = new AtomicBoolean(false);
    private String lastStatus = "";

    public Bookkeeper(final BlockingQueue<Execution> executionBlockingQueue,
                      final int numThreads, final boolean expireOrders,
                      final List<Accounts.Account> accounts,
                      final List<Stocks.Stock> stocks,
                      final int startingCash) {
        this.executionBlockingQueue = executionBlockingQueue;
        if (expireOrders) {
            orderStatusCache = CacheBuilder.newBuilder().initialCapacity(10)
                    .expireAfterAccess(2, TimeUnit.MINUTES).build();
        } else {
            orderStatusCache = CacheBuilder.newBuilder().initialCapacity(10)
                    .build();
        }
        for (final Stocks.Stock stock : stocks) {
            for (final Accounts.Account account : accounts) {
                if (stock.getVenue().equals(account.getVenue())) {
                    final PositionStatus positionStatus = new PositionStatus();
                    if (stockAccountPositionMap.containsKey(stock)) {
                        final Map<String, PositionStatus> accountPositionMap =
                                stockAccountPositionMap.get(stock);
                        accountPositionMap.put(account.getId(), positionStatus);
                    } else {
                        Map<String, PositionStatus> accountPositionMap =
                                new HashMap<>();
                        accountPositionMap.put(account.getId(), positionStatus);
                        stockAccountPositionMap.put(stock, accountPositionMap);
                    }
                }
            }
        }
        cashStatus = new CashStatus(startingCash);
        this.workers = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i += 1) {
            Thread t = new Thread(new Worker());
            logger.debug("Starting worker {}", i);
            t.start();
            workers.add(t);
        }
    }

    @Override
    public void close() {
        done.set(true);
        for (final Thread worker : workers) {
            try {
                logger.debug("Stopping worker.");
                worker.join();
            } catch (InterruptedException e) {
                logger.error("Error stopping worker.", e);
            }
        }
    }

    public void logStatus(final Map<Stocks.Stock, QuoteStatistics>
                                  quoteStatisticsMap) {
        Integer netAssetValue = 0;
        boolean first = true;
        final StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Stocks.Stock, Map<String, PositionStatus>> entry :
                stockAccountPositionMap.entrySet()) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(' ');
            }
            final Stocks.Stock stock = entry.getKey();
            stringBuilder.append(stock).append(' ');
            final Map<String, PositionStatus> accountPositionMap =
                    entry.getValue();
            boolean anotherFirst = true;
            for (final Map.Entry<String, PositionStatus> accountPosition :
                    accountPositionMap.entrySet()) {
                if (anotherFirst) {
                    anotherFirst = false;
                } else {
                    stringBuilder.append(' ');
                }
                final String account = accountPosition.getKey();
                final PositionSnapshot positionSnapshot =
                        accountPosition.getValue().getPositionSnapshot();
                final int position = positionSnapshot.getPosition();
                stringBuilder.append(account).append(": Position: ").append
                        (position)
                        .append(" Average Share Price: ").append(
                        Utilities.toCurrencyString(
                                positionSnapshot.getAverageSharePrice()));
                final QuoteStatistics quoteStatistics =
                        quoteStatisticsMap.get(stock);
                if (quoteStatistics == null) {
                    logger.warn("No last price for {}.", stock);
                } else {
                    final Integer last = quoteStatistics.getLast();
                    if (last == null) {
                        logger.warn("No last price for {}.", stock);
                    } else {
                        netAssetValue += last * position;
                        stringBuilder.append(" Last: ").append(
                                Utilities.toCurrencyString(last));
                    }
                }
            }
        }
        if (stockAccountPositionMap.size() > 0) {
            stringBuilder.append(' ');
        }
        final Integer cash = cashStatus.getCash();
        netAssetValue += cash;
        stringBuilder.append("Cash: ").append(Utilities.toCurrencyString(cash))
                .append(" NAV: ")
                .append(Utilities.toCurrencyString(netAssetValue));
        final String status = stringBuilder.toString();
        if (!status.equals(lastStatus)) {
            logger.info(status);
            lastStatus = status;
        }
    }

    public PositionSnapshot getPositionSnapshot(final Stocks.Stock stock,
                                                final String account) {
        return getPositionStatus(stock, account).getPositionSnapshot();
    }

    public void recordOrder(final NewOrderRequest newOrderRequest,
                            final NewOrderResponse newOrderResponse,
                            final OrderStatusContainer
                                    orderStatusContainer) {
        final Order order = newOrderResponse.getOrder();
        logger.debug(order.toString());
        final OrderStatus orderStatus = newOrderRequest.getOrderStatus();
        final Integer id = order.getId();
        orderStatus.setId(id);
        while (true) {
            try {
                orderStatusContainer.setOrderStatus(
                        orderStatusCache.get(id, () -> orderStatus));
                break;
            } catch (ExecutionException e) {
                logger.error("Cache insertion error.", e);
            }
        }
        final OrderType orderType = order.getOrderType();
        if (orderType == OrderType.LIMIT || orderType == OrderType.MARKET) {
            return;
        }
        final Stocks.Stock stock = orderStatus.getStock();
        final String account = orderStatus.getAccount();
        final PositionStatus positionStatus = getPositionStatus(stock, account);
        final Direction direction = order.getDirection();

        int sharePriceValue = 0;
        int filled = 0;
        List<Fill> fills = order.getFills();
        if (fills != null && fills.size() > 0) {
            ZonedDateTime lastFilled = orderStatus.getLastFilled();
            for (Fill fill : fills) {
                ZonedDateTime ts = fill.getTimestamp();
                if (lastFilled == null || ts.isAfter(lastFilled)) {
                    orderStatus.setLastFilled(ts);
                    lastFilled = ts;
                }
                int p = fill.getPrice();
                int q = fill.getQuantity();
                logger.info("{} {} share(s) of {} @ {} for {}.",
                        direction.name(), q, stock,
                        Utilities.toCurrencyString(p), account);
                filled += q;
                sharePriceValue += p * q;
            }
            if (direction == Direction.BUY) {
                cashStatus.modifyCash(-sharePriceValue);
                positionStatus.modifyPosition(filled, sharePriceValue);
            } else { // SELL
                cashStatus.modifyCash(sharePriceValue);
                positionStatus.modifyPosition(-filled, sharePriceValue);
            }
            orderStatus.update(filled, sharePriceValue);
            if (!order.getOpen()) {
                orderStatusCache.invalidate(id);
            }
        }
    }

    private PositionStatus getPositionStatus(final Stocks.Stock stock,
                                             final String account) {
        final PositionStatus positionStatus;
        if (stockAccountPositionMap.containsKey(stock)) {
            final Map<String, PositionStatus> accountPositionMap =
                    stockAccountPositionMap.get(stock);
            if (accountPositionMap.containsKey(account)) {
                positionStatus = accountPositionMap.get(account);
            } else {
                positionStatus = new PositionStatus();
                accountPositionMap.put(account, positionStatus);
            }
        } else {
            positionStatus = new PositionStatus();
            Map<String, PositionStatus> accountPositionMap =
                    new HashMap<>();
            accountPositionMap.put(account, positionStatus);
            stockAccountPositionMap.put(stock, accountPositionMap);
        }
        return positionStatus;
    }

    public void invalidateOrder(final int id) {
        orderStatusCache.invalidate(id);
    }

    public int getCash() {
        return cashStatus.getCash();
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            logger.debug("Worker started.");
            while (true) {
                try {
                    final Execution execution = executionBlockingQueue.poll(10,
                            TimeUnit.MILLISECONDS);
                    if (execution == null) {
                        if (done.get()) {
                            break;
                        } else {
                            continue;
                        }
                    }
                    final Order order = execution.getOrder();

                    final OrderType orderType = order.getOrderType();
                    if (orderType != OrderType.LIMIT &&
                            orderType != OrderType.MARKET) {
                        continue;
                    }

                    final String orderAccount = order.getAccount();
                    final String orderVenue = order.getVenue();
                    final String orderSymbol = order.getSymbol();
                    final Integer orderId = order.getId();
                    final Direction direction = order.getDirection();

                    final String executionAccount = execution.getAccount();
                    final String executionVenue = execution.getVenue();
                    final String executionSymbol = execution.getSymbol();

                    if (!executionAccount.equals(orderAccount) ||
                            !executionVenue.equals(orderVenue) ||
                            !executionSymbol.equals(orderSymbol)) {
                        logger.warn("Mismatch {}:{}:{} {}:{}:{}",
                                executionAccount, executionVenue,
                                executionSymbol, orderAccount, orderVenue,
                                orderSymbol);
                        continue;
                    }

                    OrderStatus orderStatus;
                    while (true) {
                        try {
                            orderStatus = orderStatusCache.get(orderId, () ->
                                    new OrderStatus(Stocks.getStock(orderVenue,
                                            orderSymbol), orderAccount,
                                            direction, order.getOrderType(),
                                            order.getPrice(),
                                            order.getOriginalQuantity()));
                            orderStatus.setId(orderId);
                            break;
                        } catch (ExecutionException e) {
                            logger.warn("Cache insertion error.", e);
                        }
                    }

                    int sharePriceValue = 0;
                    int filled = 0;
                    List<Fill> fills = order.getFills();
                    if (fills == null || fills.size() == 0) {
                        logger.warn("Received an execution with no fills.");
                        continue;
                    }
                    ZonedDateTime lastFilled = orderStatus.getLastFilled();
                    final Stocks.Stock stock = Stocks.getStock(executionVenue,
                            executionSymbol);
                    for (Fill fill : fills) {
                        ZonedDateTime ts = fill.getTimestamp();
                        if (lastFilled == null || ts.isAfter(lastFilled)) {
                            orderStatus.setLastFilled(ts);
                            lastFilled = ts;
                        } else {
                            logger.debug("Ignoring old fill.");
                            continue;
                        }
                        int p = fill.getPrice();
                        int q = fill.getQuantity();
                        logger.info("{} {} shares of {} @ {} for {}.",
                                direction.name(), q, stock,
                                Utilities.toCurrencyString(p), orderAccount);
                        filled += q;
                        sharePriceValue += p * q;
                    }

                    final PositionStatus positionStatus =
                            getPositionStatus(stock, orderAccount);
                    if (direction == Direction.BUY) {
                        cashStatus.modifyCash(-sharePriceValue);
                        positionStatus.modifyPosition(filled, sharePriceValue);
                    } else { // SELL
                        cashStatus.modifyCash(sharePriceValue);
                        positionStatus.modifyPosition(-filled, sharePriceValue);
                    }
                    orderStatus.update(filled, sharePriceValue);
                    if (orderId.equals(execution.getIncomingId()) &&
                            execution.getIncomingComplete() ||
                            orderId.equals(execution.getStandingId()) &&
                                    execution.getStandingComplete()) {
                        // Remove the executions that are completely filled
                        orderStatusCache.invalidate(orderId);
                    }
                } catch (InterruptedException e) {
                    logger.error("Interrupted.", e);
                }
            }
            logger.debug("Worker stopped.");
        }
    }
}
