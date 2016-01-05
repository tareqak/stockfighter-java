package levels;

import gm.LevelManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class StockfighterLevel {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String name;

    public StockfighterLevel(final String name) {
        this.name = name;
    }

    protected abstract void actuallyPlay(final CloseableHttpClient httpClient,
                                         final LevelManager levelManager);

    public void play() {
        final PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10);
        try (final CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager).build();
             final LevelManager levelManager =
                     new LevelManager(httpClient, name)) {
            actuallyPlay(httpClient, levelManager);
        } catch (IOException e) {
            logger.error("Error.", e);
        }
    }

    public String getName() {
        return name;
    }

    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            logger.error("Interrupted while sleeping.", e);
        }
    }
}
