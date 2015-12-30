package ob.backoffice;

import ob.abstractions.QuoteStatistics;
import ob.backoffice.abstractions.Account;
import ob.backoffice.abstractions.OrderStatusContainer;
import ob.backoffice.abstractions.PositionSnapshot;
import ob.backoffice.abstractions.Stock;
import ob.backoffice.websocket.ExecutionReceiver;
import ob.backoffice.websocket.QuoteReceiver;
import ob.backoffice.websocket.abstractions.Execution;
import ob.backoffice.websocket.abstractions.Quote;
import ob.requests.NewOrderRequest;
import ob.responses.NewOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BackOfficeManager implements Closeable {
    private static final Logger logger =
            LoggerFactory.getLogger(BackOfficeManager.class);
    private static final int BOOKKEEPER_WORKERS_PER_ACCOUNT = 4;

    // TODO: Assuming number of accounts == number of venues
    // per account/venue
    private final List<QuoteReceiver> quoteReceivers;
    private final List<ExecutionReceiver> executionReceivers;
    private final List<Future<Boolean>> futures;

    // one
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final Bookkeeper bookkeeper;
    private final ExecutorService quoteReceiverPool;
    private final Map<Stock, QuoteStatistics> quoteStatisticsMap =
            new ConcurrentHashMap<>();

    public BackOfficeManager(final List<Account> accounts,
                             final List<Stock> stocks,
                             final int startingCash,
                             final boolean useExecutionReceiver,
                             final boolean expireOrders,
                             final boolean useQuoteReceiver) {
        final int numThreads;
        final BlockingQueue<Execution> executionBlockingQueue;
        final int numAccounts = accounts.size();
        if (useExecutionReceiver) {
            numThreads = BOOKKEEPER_WORKERS_PER_ACCOUNT * numAccounts;
            executionBlockingQueue = new LinkedBlockingQueue<>();
            this.executionReceivers = new ArrayList<>(numAccounts);
            this.executionReceivers.addAll(accounts.stream().map(
                    account -> new ExecutionReceiver(executionBlockingQueue,
                            account.getId(), account.getVenue()))
                    .collect(Collectors.toList()));
        } else {
            numThreads = 0;
            executionBlockingQueue = null;
            this.executionReceivers = null;
        }
        if (useQuoteReceiver) {
            this.quoteReceivers = new ArrayList<>(numAccounts);
            this.quoteReceivers.addAll(accounts.stream().map(
                    account -> new QuoteReceiver(account.getId(),
                            account.getVenue()))
                    .collect(Collectors.toList()));
            this.quoteReceiverPool =
                    Executors.newFixedThreadPool(numAccounts);
            this.futures = new ArrayList<>(numAccounts);
        } else {
            this.quoteReceivers = null;
            this.quoteReceiverPool = null;
            this.futures = null;
        }
        this.bookkeeper = new Bookkeeper(executionBlockingQueue, numThreads,
                expireOrders, accounts, stocks, startingCash);
        for (final Stock stock : stocks) {
            quoteStatisticsMap.put(stock, new QuoteStatistics());
        }
    }

    public void gatherStatistics() {
        if (quoteReceivers != null) {
            for (final QuoteReceiver quoteReceiver : quoteReceivers) {
                futures.add(
                        quoteReceiverPool.submit(retrieveQuote(quoteReceiver)));
            }
            for (final Future<Boolean> future : futures) {
                if (future == null) {
                    logger.warn("Null future.");
                } else {
                    try {
                        future.get();
                    } catch (Exception e) {
                        logger.error("Error getting future.", e);
                    }
                }
            }
            futures.clear();
        }
    }

    public void logStatus() {
        bookkeeper.logStatus(quoteStatisticsMap);
    }

    public Map<Stock, QuoteStatistics> getQuoteStatisticsMap() {
        return quoteStatisticsMap;
    }

    public void recordOrder(final NewOrderRequest newOrderRequest,
                            final NewOrderResponse newOrderResponse,
                            final OrderStatusContainer orderStatusContainer) {
        bookkeeper.recordOrder(newOrderRequest,
                newOrderResponse, orderStatusContainer);
    }

    public PositionSnapshot getPositionSnapshot(final Stock stock,
                                                final String account) {
        return bookkeeper.getPositionSnapshot(stock, account);
    }

    public void invalidateOrder(final int id) {
        bookkeeper.invalidateOrder(id);
    }

    public int getCash() {
        return bookkeeper.getCash();
    }

    @Override
    public void close() {
        done.set(true);
        if (quoteReceivers != null) {
            quoteReceivers.forEach(QuoteReceiver::close);
            quoteReceiverPool.shutdown();
        }
        if (executionReceivers != null) {
            executionReceivers.forEach(ExecutionReceiver::close);
        }
        bookkeeper.close();
    }

    private Callable<Boolean> retrieveQuote(final QuoteReceiver quoteReceiver) {
        return () -> {
            for (final Map.Entry<Stock, Quote> stockQuoteEntry :
                    quoteReceiver.getStockQuoteMap().entrySet()) {
                final Stock stock = stockQuoteEntry.getKey();
                final Quote quote = stockQuoteEntry.getValue();
                final QuoteStatistics quoteStatistics;
                if (quoteStatisticsMap.containsKey(stock)) {
                    quoteStatistics = quoteStatisticsMap.get(stock);
                } else {
                    quoteStatistics = new QuoteStatistics();
                    quoteStatisticsMap.put(stock, quoteStatistics);
                }
                if (quoteStatistics.processQuote(quote)) {
                    logger.debug("Quote: {}", quote);
                }
            }
            return true;
        };
    }
}
