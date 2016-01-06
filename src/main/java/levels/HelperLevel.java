package levels;

import gm.LevelManager;
import gm.responses.LevelResponse;
import ob.backoffice.BackOfficeManager;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.atomic.AtomicBoolean;

public class HelperLevel extends StockfighterLevel {
    public HelperLevel(final String name, final AtomicBoolean done) {
        super(name, done);
    }

    @Override
    protected void play(final CloseableHttpClient httpClient,
                        final LevelManager levelManager) {
        final boolean useExecutionReceiver = true;
        final boolean expireOrders = false;
        final boolean useQuoteReceiver = true;
        final LevelResponse levelResponse = levelManager.getLevelResponse();
        final String venue = levelResponse.getVenues().get(0);
        final String accountId = levelResponse.getAccount();
        final String symbol = levelResponse.getTickers().get(0);
        stocks.getStock(venue, symbol);
        accounts.getAccount(venue, accountId);
        final int startingCash = levelResponse.getBalances().get("USD");
        try (final BackOfficeManager backOfficeManager =
                     new BackOfficeManager(done, accounts, stocks, startingCash,
                             useExecutionReceiver, expireOrders,
                             useQuoteReceiver)) {
            while (!levelManager.isLevelComplete()) {
                backOfficeManager.gatherStatistics();
                backOfficeManager.logStatus();
            }
        }
    }
}
