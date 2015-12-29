package levels;

import http.StockfighterHttpRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class StockfighterLevel {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract void actuallyPlay();

    public void play() {
        final PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10);
        try (final CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager).build()) {
            StockfighterHttpRequest.setHttpClient(httpClient);
            actuallyPlay();
        } catch (IOException e) {
            logger.error("Error.", e);
        }
    }

    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            logger.error("Interrupted while sleeping.", e);
        }
    }
}
