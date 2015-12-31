package ob.backoffice;

import ob.abstractions.QuoteStatistics;
import ob.backoffice.abstractions.Accounts;
import ob.backoffice.abstractions.OrderStatusContainer;
import ob.backoffice.abstractions.Stocks;
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
import java.util.function.Function;
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
    private final Map<Stocks.Stock, QuoteStatistics> quoteStatisticsMap;

    public BackOfficeManager(final List<Accounts.Account> accounts,
                             final List<Stocks.Stock> stocks,
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
            this.executionReceivers = accounts.parallelStream().map(
                    account -> new ExecutionReceiver(executionBlockingQueue,
                            account.getId(), account.getVenue()))
                    .collect(Collectors.toList());
        } else {
            numThreads = 0;
            executionBlockingQueue = null;
            this.executionReceivers = null;
        }
        if (useQuoteReceiver) {
            this.quoteReceivers = accounts.parallelStream().map(account ->
                    new QuoteReceiver(account.getId(), account.getVenue()))
                    .collect(Collectors.toList());
            this.quoteReceiverPool = Executors.newFixedThreadPool(numAccounts);
            this.futures = new ArrayList<>(numAccounts);
        } else {
            this.quoteReceivers = null;
            this.quoteReceiverPool = null;
            this.futures = null;
        }
        this.bookkeeper = new Bookkeeper(executionBlockingQueue, numThreads,
                expireOrders, startingCash, accounts, stocks);
        quoteStatisticsMap = stocks.parallelStream().collect(
                Collectors.toConcurrentMap(Function.identity(),
                        stock -> new QuoteStatistics()));
    }

    public void gatherStatistics() {
        if (quoteReceivers != null) {
            quoteReceivers.parallelStream().forEach(quoteReceiver ->
                    futures.add(quoteReceiverPool.submit(
                            retrieveQuote(quoteReceiver))));
            futures.parallelStream().filter(f -> f != null)
                    .forEach(f -> {
                        try {
                            f.get();
                        } catch (Exception e) {
                            logger.error("Error getting future.", e);
                        }
                    });
            futures.clear();
        }
    }

    public void logStatus() {
        bookkeeper.logStatus(quoteStatisticsMap);
    }

    public Map<Stocks.Stock, QuoteStatistics> getQuoteStatisticsMap() {
        return quoteStatisticsMap;
    }

    public void recordOrder(final NewOrderRequest newOrderRequest,
                            final NewOrderResponse newOrderResponse,
                            final OrderStatusContainer orderStatusContainer) {
        bookkeeper.recordOrder(newOrderRequest,
                newOrderResponse, orderStatusContainer);
    }

    public Bookkeeper.PositionStatus.PositionSnapshot getPositionSnapshot(
            final Stocks.Stock stock, final Accounts.Account account) {
        return bookkeeper.getPositionSnapshot(account, stock);
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
            quoteReceiver.getStockQuoteMap().entrySet().parallelStream()
                    .forEach(entry -> {
                        final Quote quote = entry.getValue();
                        final QuoteStatistics quoteStatistics =
                                quoteStatisticsMap.get(entry.getKey());
                        if (quoteStatistics.processQuote(quote)) {
                            logger.debug("Quote: {}", quote);
                        }
                    });
            return true;
        };
    }
}
