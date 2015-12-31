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
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Bookkeeper implements Closeable {
    private static final Logger logger =
            LoggerFactory.getLogger(Bookkeeper.class);

    private final BlockingQueue<Execution> executionBlockingQueue;
    private final Map<Stocks.Stock, Orderbook> orderbooks;
    private final Map<Accounts.Account, Map<Stocks.Stock, PositionStatus>>
            positionStatusMap;
    private final Cache<Integer, OrderStatus> orderStatusCache;
    private final CashStatus cashStatus;
    private final List<Thread> workers;
    private final AtomicBoolean done = new AtomicBoolean(false);
    private String lastStatus = "";

    public Bookkeeper(final BlockingQueue<Execution> executionBlockingQueue,
                      final int numThreads, final boolean expireOrders,
                      final int startingCash,
                      final List<Accounts.Account> accounts,
                      final List<Stocks.Stock> stocks) {
        this.executionBlockingQueue = executionBlockingQueue;
        if (expireOrders) {
            orderStatusCache = CacheBuilder.newBuilder().initialCapacity(10)
                    .expireAfterAccess(2, TimeUnit.MINUTES).build();
        } else {
            orderStatusCache = CacheBuilder.newBuilder().initialCapacity(10)
                    .build();
        }
        cashStatus = new CashStatus(startingCash);

        positionStatusMap = accounts.parallelStream().collect(Collectors.toMap(
                Function.identity(), account -> new HashMap<>()));
        orderbooks = stocks.parallelStream().collect(Collectors.toMap(
                Function.identity(), stock -> new Orderbook()));
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
        workers.parallelStream().forEach(worker -> {
            try {
                logger.debug("Stopping worker.");
                worker.join();
            } catch (InterruptedException e) {
                logger.error("Error stopping worker.", e);
            }
        });
    }

    public void logStatus(final Map<Stocks.Stock, QuoteStatistics>
                                  quoteStatisticsMap) {
        final StringBuilder stringBuilder = new StringBuilder();
        // nav is net asset value
        int nav = positionStatusMap.entrySet().parallelStream().mapToInt(e -> {
            final Accounts.Account account = e.getKey();
            stringBuilder.append(account).append(' ');
            return e.getValue().entrySet().parallelStream().mapToInt(f -> {
                final Stocks.Stock stock = f.getKey();
                final PositionStatus.PositionSnapshot positionSnapshot =
                        f.getValue().getPositionSnapshot();
                final int position = positionSnapshot.getPosition();
                stringBuilder.append(stock.getSymbol()).append(": Position: ")
                        .append(position)
                        .append(" Average Share Price: ").append(
                        Utilities.toCurrencyString(
                                positionSnapshot.getAverageSharePrice()));
                final QuoteStatistics quoteStatistics =
                        quoteStatisticsMap.get(stock);
                if (quoteStatistics == null) {
                    logger.debug("No statistics for {}.", stock);
                } else {
                    final Integer last = quoteStatistics.getLast();
                    if (last == null) {
                        logger.debug("No last price for {}.", stock);
                    } else {
                        stringBuilder.append(" Last: ").append(
                                Utilities.toCurrencyString(last));
                        return last * position;
                    }
                }
                return 0;
            }).sum();
        }).sum();
        stringBuilder.append(' ');
        final Integer cash = cashStatus.getCash();
        nav += cash;
        stringBuilder.append("Cash: ").append(Utilities.toCurrencyString(cash))
                .append(" NAV: ")
                .append(Utilities.toCurrencyString(nav));
        final String status = stringBuilder.toString();
        if (!status.equals(lastStatus)) {
            logger.info(status);
            lastStatus = status;
        }
    }

    public PositionStatus.PositionSnapshot getPositionSnapshot(
            final Accounts.Account account, final Stocks.Stock stock) {
        return getPositionStatus(account, stock).getPositionSnapshot();
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
        final Accounts.Account account = orderStatus.getAccount();
        final Direction direction = order.getDirection();
        final ZonedDateTime timestamp = order.getTimestamp();
        processFills(order.getFills(), stock, account, id, direction, orderType,
                timestamp, orderStatus);
        if (!order.getOpen()) {
            orderStatusCache.invalidate(id);
        }
    }

    private PositionStatus getPositionStatus(final Accounts.Account account,
                                             final Stocks.Stock stock) {
        final Map<Stocks.Stock, PositionStatus> map =
                positionStatusMap.get(account);
        if (map.containsKey(stock)) {
            return map.get(stock);
        } else {
            final PositionStatus positionStatus = new PositionStatus();
            map.put(stock, positionStatus);
            return positionStatus;
        }
    }

    private void processFills(final List<Fill> fills, final Stocks.Stock stock,
                              final Accounts.Account account, final Integer id,
                              final Direction direction,
                              final OrderType orderType,
                              final ZonedDateTime lastFilled,
                              final OrderStatus orderStatus) {
        if (fills != null && fills.size() > 0) {
            final PositionStatus positionStatus =
                    getPositionStatus(account, stock);
            final Direction opposite = direction == Direction.BUY ?
                    Direction.SELL : Direction.BUY;
            final Orderbook orderbook = orderbooks.get(stock);
            final FillResult result = fills.parallelStream()
                    .filter(f -> lastFilled == null ||
                            f.getTimestamp().isAfter(lastFilled) ||
                            f.getTimestamp().isEqual(lastFilled))
                    .reduce(new FillResult(), (fillResult, fill) -> {
                        final int p = fill.getPrice();
                        final int q = fill.getQuantity();
                        logger.info("{} {} share(s) of {} @ {} for {} ({}:{}).",
                                direction.name(), q, stock,
                                Utilities.toCurrencyString(p), account.getId(),
                                id, orderType.name());
                        final ZonedDateTime ts = fill.getTimestamp();
                        orderbook.addEntry(p, q, direction, ts, account);
                        orderbook.addEntry(p, q, opposite, ts, null);
                        return new FillResult(q, p * q, ts);
                    }, (a, b) -> {
                        final ZonedDateTime timestamp;
                        if (a.timestamp == null ||
                                b.timestamp.isAfter(a.timestamp)) {
                            timestamp = b.timestamp;
                        } else {
                            timestamp = a.timestamp;
                        }
                        final int totalFilled =
                                a.totalFilled + b.totalFilled;
                        final int sharePriceValue =
                                a.sharePriceValue + b.sharePriceValue;
                        return new FillResult(totalFilled,
                                sharePriceValue, timestamp);
                    });
            final int sharePriceValue = result.sharePriceValue;
            final int filled = result.totalFilled;
            if (direction == Direction.BUY) {
                cashStatus.modifyCash(-sharePriceValue);
                positionStatus.modifyPosition(filled, sharePriceValue);
            } else { // SELL
                cashStatus.modifyCash(sharePriceValue);
                positionStatus.modifyPosition(-filled, sharePriceValue);
            }
            orderStatus.update(filled, sharePriceValue, result.timestamp);
        }
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

                    final Accounts.Account orderAccount = order.getAccount();
                    final Stocks.Stock orderStock = order.getStock();
                    final Integer orderId = order.getId();
                    final Direction direction = order.getDirection();

                    final Accounts.Account executionAccount =
                            execution.getAccount();
                    final Stocks.Stock executionStock = execution.getStock();

                    if (!executionAccount.equals(orderAccount) ||
                            !executionStock.equals(orderStock)) {
                        logger.warn("Mismatch {}:{} {}:{}",
                                executionAccount, orderAccount,
                                executionStock, orderStock);
                        continue;
                    }

                    OrderStatus orderStatus;
                    while (true) {
                        try {
                            orderStatus = orderStatusCache.get(orderId, () ->
                                    new OrderStatus(orderStock, orderAccount,
                                            direction, order.getOrderType(),
                                            order.getPrice(),
                                            order.getOriginalQuantity()));
                            orderStatus.setId(orderId);
                            break;
                        } catch (ExecutionException e) {
                            logger.warn("Cache insertion error.", e);
                        }
                    }
                    processFills(order.getFills(), orderStock, orderAccount,
                            orderId, direction, orderType,
                            execution.getFilledAt(), orderStatus);
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

    public class PositionStatus {
        private final ReentrantReadWriteLock positionReentrantReadWriteLock =
                new ReentrantReadWriteLock();
        private int position = 0;
        private int sharePriceValue = 0;

        private PositionStatus() {
        }

        public PositionSnapshot getPositionSnapshot() {
            try {
                positionReentrantReadWriteLock.readLock().lock();
                return new PositionSnapshot(this, position, sharePriceValue);
            } finally {
                positionReentrantReadWriteLock.readLock().unlock();
            }
        }

        public void modifyPosition(final Integer shares,
                                   final Integer incomingSharePriceValue) {
            positionReentrantReadWriteLock.writeLock().lock();
            if (position >= 0) {
                if (shares > 0) {
                    // BUY
                    sharePriceValue += incomingSharePriceValue;
                    position += shares;
                } else if (Math.abs(shares) <= position) {
                    // SELL but position will stay 0 or more
                    if (position > 0) {
                        sharePriceValue += sharePriceValue / position * shares;
                        position += shares;
                    }
                } else {
                    // SELL into a short position
                    int averageSharePrice = incomingSharePriceValue / shares;
                    position += shares;
                    sharePriceValue = averageSharePrice * position;
                }
            } else {
                if (shares < 0) {
                    // SELL
                    sharePriceValue += incomingSharePriceValue;
                    position += shares;
                } else if (Math.abs(position) >= shares) {
                    // BUY but but position will stay 0 or less
                    sharePriceValue += sharePriceValue / position * shares;
                    position += shares;
                } else {
                    // BUY into a long position
                    if (shares > 0) {
                        int averageSharePrice = incomingSharePriceValue /
                                shares;
                        position += shares;
                        sharePriceValue = Math.abs(averageSharePrice *
                                position);
                    }
                }
            }
            positionReentrantReadWriteLock.writeLock().unlock();
        }

        public class PositionSnapshot {
            private final int position;
            private final int sharePriceValue;
            private final PositionStatus positionStatus;

            private PositionSnapshot(final PositionStatus positionStatus,
                                     final int position,
                                     final int sharePriceValue) {
                this.position = position;
                this.sharePriceValue = sharePriceValue;
                this.positionStatus = positionStatus;
            }

            public int getPosition() {
                return position;
            }

            public int getSharePriceValue() {
                return sharePriceValue;
            }

            public int getAverageSharePrice() {
                if (position == 0) {
                    return 0;
                }
                return Math.abs(sharePriceValue / position);
            }

            public PositionSnapshot getNewSnapshot() {
                return positionStatus.getPositionSnapshot();
            }
        }
    }

    public class Orderbook {
        private final Queue<OrderbookEntry> buys =
                new ConcurrentLinkedQueue<>();
        private final Queue<OrderbookEntry> sells =
                new ConcurrentLinkedQueue<>();
        private ZonedDateTime timestamp = null;

        public void addEntry(final int price, final int quantity,
                             final Direction direction,
                             final ZonedDateTime timestamp,
                             final Accounts.Account account) {
            final OrderbookEntry orderbookEntry =
                    new OrderbookEntry(price, quantity, timestamp, account);
            switch (direction) {
                case BUY:
                    buys.add(orderbookEntry);
                    break;
                case SELL:
                    sells.add(orderbookEntry);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown direction: " +
                            direction);
            }
            if (this.timestamp == null || this.timestamp.isBefore(timestamp)) {
                this.timestamp = timestamp;
            }
        }

        private class OrderbookEntry {
            private final int price;
            private final int quantity;
            private final ZonedDateTime timestamp;
            private final Accounts.Account account;

            public OrderbookEntry(final int price,
                                  final int quantity,
                                  final ZonedDateTime timestamp,
                                  final Accounts.Account account) {
                this.price = price;
                this.quantity = quantity;
                this.timestamp = timestamp;
                this.account = account;
            }

            public int getPrice() {
                return price;
            }

            public int getQuantity() {
                return quantity;
            }

            public ZonedDateTime getTimestamp() {
                return timestamp;
            }

            @Override
            public String toString() {
                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(quantity).append(" @ ")
                        .append(Utilities.toCurrencyString(price))
                        .append(" on: ").append(timestamp).append(" by ")
                        .append(account);
                return stringBuilder.toString();
            }
        }
    }

    private class FillResult {
        int totalFilled = 0;
        int sharePriceValue = 0;
        ZonedDateTime timestamp = null;

        public FillResult() {
        }

        public FillResult(final int totalFilled,
                          final int sharePriceValue,
                          final ZonedDateTime timestamp) {
            this.totalFilled = totalFilled;
            this.sharePriceValue = sharePriceValue;
            this.timestamp = timestamp;
        }
    }
}
