package ob.backoffice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import ob.backoffice.websocket.abstractions.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutionReceiver implements Closeable {
    private static final Logger logger =
            LoggerFactory.getLogger(ExecutionReceiver.class);
    private static final String baseUrl =
            "wss://api.stockfighter.io/ob/api/ws/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String url;
    private final BlockingQueue<Execution> executionBlockingQueue;
    private final WebSocketContainer webSocketContainer =
            ContainerProvider.getWebSocketContainer();
    private final AtomicBoolean done = new AtomicBoolean(false);
    private Session session;

    public ExecutionReceiver(final BlockingQueue<Execution>
                                     executionBlockingQueue,
                             final String account, final String venue) {
        url = baseUrl + account + "/venues/" + venue + "/executions";
        this.executionBlockingQueue = executionBlockingQueue;
        connect();
    }

    public ExecutionReceiver(final BlockingQueue<Execution>
                                     executionBlockingQueue,
                             final String account, final String venue,
                             final String stock) {
        url = baseUrl + account + "/venues/" + venue + "/executions/stocks/"
                + stock;
        this.executionBlockingQueue = executionBlockingQueue;
        connect();
    }

    private void connect() {
        while (true) {
            try {
                logger.debug("Attempting to connect to {}", url);
                session = webSocketContainer.connectToServer(
                        new ExecutionsWebSocket(this), new URI(url));
                break;
            } catch (Exception e) {
                logger.error("Error setting up WebSocket.", e);
            }
        }
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

    private class ExecutionsWebSocket extends Endpoint {
        private final ExecutionReceiver executionReceiver;

        public ExecutionsWebSocket(ExecutionReceiver executionReceiver) {
            super();
            this.executionReceiver = executionReceiver;
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            logger.info("Stream closed. {}", closeReason.getReasonPhrase());
            super.onClose(session, closeReason);

            // Reconnect if we are not done
            if (!done.get()) {
                executionReceiver.connect();
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
                        logger.debug("Execution Message: {}", message);
                        final Execution execution = objectMapper.readValue
                                (message, Execution.class);
                        if (execution != null) {
                            executionBlockingQueue.put(execution);
                        }
                    } catch (Exception e) {
                        logger.error("Error reading execution.", e);
                    }
                }
            });
        }
    }
}