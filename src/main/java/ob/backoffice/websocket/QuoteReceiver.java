package ob.backoffice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import ob.backoffice.websocket.abstractions.Quote;
import ob.backoffice.websocket.abstractions.TickerTape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class QuoteReceiver implements Closeable {
    private static final Logger logger =
            LoggerFactory.getLogger(QuoteReceiver.class);
    private static final String baseUrl =
            "wss://www.stockfighter.io/ob/api/ws/";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String url;
    private final ReentrantReadWriteLock reentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private final WebSocketContainer webSocketContainer =
            ContainerProvider.getWebSocketContainer();
    private final AtomicBoolean done = new AtomicBoolean(false);
    private Quote quote = null;
    private AtomicBoolean newQuote = new AtomicBoolean(false);
    private Session session;

    public QuoteReceiver(final String account, final String venue) {
        url = baseUrl + account + "/venues/" + venue + "/tickertape";
        connect();
    }

    public QuoteReceiver(final String account, final String venue,
                         final String stock) {
        url = baseUrl + account + "/venues/" + venue + "/tickertape/stocks/"
                + stock;
        connect();
    }

    public void connect() {
        while (true) {
            try {
                logger.debug("Attempting to connect to {}", url);
                session = webSocketContainer.connectToServer(
                        new QuotesWebSocket(this), new URI(url));
                break;
            } catch (Exception e) {
                logger.error("Error setting up WebSocket.", e);
            }
        }
    }

    public Quote getQuote() {
        try {
            while (true) {
                if (newQuote.get()) {
                    newQuote.set(false);
                    break;
                }
            }
            reentrantReadWriteLock.readLock().lock();
            return quote;
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }

    private void setQuote(Quote quote) {
        reentrantReadWriteLock.writeLock().lock();
        this.quote = quote;
        reentrantReadWriteLock.writeLock().unlock();
        newQuote.set(true);
    }

    @Override
    public void close() {
        done.set(true);
        try {
            logger.info("Closing WebSocket.");
            session.close();
        } catch (IOException e) {
            logger.error("Error closing WebSocket.", e);
        }
    }

    private class QuotesWebSocket extends Endpoint {
        private final QuoteReceiver quoteReceiver;
        private boolean lastQuoteIsNull = true;
        private ZonedDateTime lastQuoteTime = null;

        public QuotesWebSocket(QuoteReceiver quoteReceiver) {
            super();
            this.quoteReceiver = quoteReceiver;
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            logger.info("Stream closed. {}", closeReason.getReasonPhrase());
            super.onClose(session, closeReason);

            // Reconnect if we are not done
            if (!done.get()) {
                quoteReceiver.connect();
            }
        }

        @Override
        public void onError(Session session, Throwable thr) {
            logger.error("Error.", thr);
            super.onError(session, thr);
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            logger.info("Started session with id {}", session.getId());

            // Do not use lambdas because they do not resolve correctly
            session.addMessageHandler(new MessageHandler.Whole<PongMessage>() {
                @Override
                public void onMessage(PongMessage message) {
                    logger.info("PONG: {}", message.toString());
                    session.getAsyncRemote().sendText("p");
                }
            });

            // Do not use lambdas because they do not resolve correctly
            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    try {
                        logger.debug("Quotes Message: {}", message);
                        final TickerTape tickerTape = objectMapper.readValue
                                (message, TickerTape.class);
                        if (tickerTape != null) {
                            final Quote quote = tickerTape.getQuote();
                            if (lastQuoteIsNull) {
                                quoteReceiver.setQuote(quote);
                                lastQuoteIsNull = false;
                            } else {
                                final ZonedDateTime currentQuoteTime =
                                        quote.getQuoteTime();
                                if (lastQuoteTime == null) {
                                    if (currentQuoteTime == null) {
                                        logger.warn("Quote time is null.");
                                    } else {
                                        quoteReceiver.setQuote(quote);
                                        lastQuoteTime = quote.getQuoteTime();
                                    }
                                } else if (currentQuoteTime != null &&
                                        currentQuoteTime.isAfter(lastQuoteTime)) {
                                    quoteReceiver.setQuote(quote);
                                    lastQuoteTime = quote.getQuoteTime();
                                } // else keep last quote (do nothing)
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error reading quote.", e);
                    }
                }
            });
        }
    }
}