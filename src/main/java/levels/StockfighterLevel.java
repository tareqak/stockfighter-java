package levels;

import gm.LevelManager;
import ob.backoffice.abstractions.Accounts;
import ob.backoffice.abstractions.Stocks;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class StockfighterLevel implements Runnable {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String name;
    protected final AtomicBoolean done;
    protected final Accounts accounts = new Accounts();
    protected final Stocks stocks = new Stocks();

    public StockfighterLevel(final String name, final AtomicBoolean done) {
        this.name = name;
        this.done = done;
    }

    protected abstract void play(final CloseableHttpClient httpClient,
                                 final LevelManager levelManager);

    @Override
    public void run() {
        final PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10);
        try (final CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager).build();
             final LevelManager levelManager =
                     new LevelManager(httpClient, name, done)) {
            play(httpClient, levelManager);
        } catch (IOException e) {
            logger.error("Error.", e);
        }
        done.set(true);
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
