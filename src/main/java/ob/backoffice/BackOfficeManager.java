package ob.backoffice;

import ob.abstractions.QuoteStatistics;
import ob.backoffice.abstractions.OrderStatusContainer;
import ob.backoffice.abstractions.Stock;
import ob.backoffice.websocket.ExecutionReceiver;
import ob.backoffice.websocket.QuoteReceiver;
import ob.backoffice.websocket.abstractions.Execution;
import ob.backoffice.websocket.abstractions.Quote;
import ob.responses.NewOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackOfficeManager implements Closeable {
    private static final Logger logger =
            LoggerFactory.getLogger(BackOfficeManager.class);
    private static final int NUMBER_OF_BOOKKEEPER_WORKERS = 4;

    // TODO: These should be lists, but for another day
    // per venue
    private final Bookkeeper bookkeeper;
    private final QuoteReceiver quoteReceiver;
    private final ExecutionReceiver executionReceiver;

    // per venue per stock
    private final QuoteStatistics quoteStatistics = new QuoteStatistics();

    // one
    private final Map<Stock, Integer> lastPriceMap = new HashMap<>();
    private final AtomicBoolean done = new AtomicBoolean(false);

    public BackOfficeManager(final String account, final String venue,
                             final String symbol,
                             final boolean useExecutionReceiver,
                             final boolean expireOrders,
                             final boolean useQuoteReceiver) {
        final int numThreads;
        final BlockingQueue<Execution> executionBlockingQueue;
        if (useExecutionReceiver) {
            numThreads = NUMBER_OF_BOOKKEEPER_WORKERS;
            executionBlockingQueue = new ArrayBlockingQueue<>(numThreads * 3);
            this.executionReceiver = new ExecutionReceiver(
                    executionBlockingQueue, account, venue, symbol);
        } else {
            numThreads = 0;
            executionBlockingQueue = null;
            this.executionReceiver = null;
        }
        if (useQuoteReceiver) {
            this.quoteReceiver = new QuoteReceiver(account, venue, symbol);
        } else {
            this.quoteReceiver = null;
        }
        this.bookkeeper = new Bookkeeper(executionBlockingQueue, numThreads,
                expireOrders);
    }

    public void gatherStatistics() {
        if (quoteReceiver != null) {
            while (true) {
                final Quote quote = quoteReceiver.getQuote();
                if (quote != null) {
                    quoteStatistics.getLast().setCurrentValue(quote.getLast());
                    final Integer value =
                            quoteStatistics.getLast().getCurrentValue();
                    if (value != null) {
                        quoteStatistics.processQuote(quote);
                        final Stock stock =
                                new Stock(quote.getVenue(), quote.getSymbol());
                        lastPriceMap.put(stock, value);
                        logger.info(quote.toString());
                        return;
                    }
                }
            }
        }
    }

    public void logStatus() {
        bookkeeper.logStatus(lastPriceMap);
    }

    public QuoteStatistics getQuoteStatistics() {
        return quoteStatistics;
    }

    public void recordOrder(final NewOrderResponse newOrderResponse,
                            final OrderStatusContainer orderStatusContainer) {
        bookkeeper.recordOrder(newOrderResponse, orderStatusContainer);
    }

    public int getPosition(final Stock stock, final String account) {
        return bookkeeper.getPosition(stock, account);
    }

    @Override
    public void close() {
        done.set(true);
        if (quoteReceiver != null) {
            quoteReceiver.close();
        }
        if (executionReceiver != null) {
            executionReceiver.close();
        }
        bookkeeper.close();
    }
}
