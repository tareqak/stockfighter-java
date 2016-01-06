package ob.backoffice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import ob.backoffice.abstractions.Stocks;
import ob.backoffice.websocket.abstractions.Quote;
import ob.backoffice.websocket.abstractions.TickerTape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuoteReceiver implements Closeable {
    private static final Logger logger =
            LoggerFactory.getLogger(QuoteReceiver.class);
    private static final String baseUrl =
            "wss://api.stockfighter.io/ob/api/ws/";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String url;
    private final WebSocketContainer webSocketContainer =
            ContainerProvider.getWebSocketContainer();
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final Map<Stocks.Stock, Quote> stockQuoteMap =
            new ConcurrentHashMap<>(10);
    private final Stocks stocks;
    private Session session;

    public QuoteReceiver(final String account, final String venue,
                         final Stocks stocks) {
        url = baseUrl + account + "/venues/" + venue + "/tickertape";
        this.stocks = stocks;
        connect();
    }

    public QuoteReceiver(final String account, final String venue,
                         final String stock, final Stocks stocks) {
        url = baseUrl + account + "/venues/" + venue + "/tickertape/stocks/"
                + stock;
        this.stocks = stocks;
        connect();
    }

    private void connect() {
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

    public Map<Stocks.Stock, Quote> getStockQuoteMap() {
        return stockQuoteMap;
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
        private final Map<Stocks.Stock, ZonedDateTime> stockZonedDateTimeMap =
                new HashMap<>();

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
            logger.info("Started session with id {}.", session.getId());
            final RemoteEndpoint.Async remote = session.getAsyncRemote();
            final String pingString = "p";
            final ByteBuffer byteBuffer = ByteBuffer.allocate(
                    pingString.getBytes().length);

            // Do not use lambdas because they do not resolve correctly
            session.addMessageHandler(new MessageHandler.Whole<PongMessage>() {
                @Override
                public void onMessage(PongMessage message) {
                    logger.info("PONG: {}",
                            new String(message.getApplicationData().array()));
                    try {
                        remote.sendPing(byteBuffer);
                    } catch (IOException e) {
                        logger.error("Error sending ping.", e);
                    }
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
                            final Stocks.Stock stock = stocks.getStock(quote
                                    .getVenue(), quote.getSymbol());
                            final ZonedDateTime currentQuoteTime =
                                    quote.getQuoteTime();
                            if (stockZonedDateTimeMap.containsKey(stock)) {
                                final ZonedDateTime lastQuoteTime =
                                        stockZonedDateTimeMap.get(stock);
                                if (lastQuoteTime == null) {
                                    if (currentQuoteTime == null) {
                                        logger.warn("Quote time is null.");
                                    } else {
                                        setQuote(stock, currentQuoteTime,
                                                quote);
                                    }
                                } else if (currentQuoteTime != null) {
                                    setQuote(stock, currentQuoteTime, quote);
                                } // else keep last quote (do nothing)
                            } else if (currentQuoteTime != null) {
                                setQuote(stock, currentQuoteTime, quote);
                            } // else keep last quote (do nothing)
                        }
                    } catch (Exception e) {
                        logger.error("Error reading quote.", e);
                    }
                }
            });
        }

        private void setQuote(final Stocks.Stock stock,
                              final ZonedDateTime currentQuoteTime,
                              final Quote quote) {
            stockZonedDateTimeMap.put(stock, currentQuoteTime);
            stockQuoteMap.put(stock, quote);
        }
    }
}