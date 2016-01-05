package levels;

import gm.LevelManager;
import gm.responses.LevelResponse;
import ob.backoffice.BackOfficeManager;
import ob.backoffice.abstractions.Accounts;
import ob.backoffice.abstractions.Stocks;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;

public class HelperLevel extends StockfighterLevel {
    public HelperLevel(final String name) {
        super(name);
    }

    @Override
    protected void actuallyPlay(final CloseableHttpClient httpClient,
                                final LevelManager levelManager) {
        final boolean useExecutionReceiver = true;
        final boolean expireOrders = false;
        final boolean useQuoteReceiver = true;
        final LevelResponse levelResponse = levelManager.getLevelResponse();
        final String venue = levelResponse.getVenues().get(0);
        final String accountId = levelResponse.getAccount();
        final String symbol = levelResponse.getTickers().get(0);
        final Stocks.Stock stock = Stocks.getStock(venue, symbol);
        Accounts.getAccount(venue, accountId);
        final List<Stocks.Stock> stocks = Stocks.getStocks();
        final List<Accounts.Account> accounts = Accounts.getAccounts();
        final int startingCash = levelResponse.getBalances().get("USD");
        try (final BackOfficeManager backOfficeManager =
                     new BackOfficeManager(accounts, stocks, startingCash,
                             useExecutionReceiver, expireOrders,
                             useQuoteReceiver)) {
            while (!levelManager.isLevelComplete()) {
                backOfficeManager.gatherStatistics();
                backOfficeManager.logStatus();
            }
        }
    }
}
