package levels;

import gm.LevelManager;
import gm.responses.LevelResponse;
import ob.backoffice.BackOfficeManager;
import ob.backoffice.abstractions.Account;
import ob.backoffice.abstractions.Stock;

import java.util.ArrayList;
import java.util.List;

public class HelperLevel extends StockfighterLevel {
    @Override
    protected void actuallyPlay() {
        final boolean useExecutionReceiver = true;
        final boolean expireOrders = false;
        final boolean useQuoteReceiver = true;
        try (LevelManager levelManager =
                     new LevelManager("first_steps")) {
            final LevelResponse levelResponse = levelManager.getLevelResponse();
            final String venue = levelResponse.getVenues().get(0);
            final String accountId = levelResponse.getAccount();
            final String symbol = levelResponse.getTickers().get(0);
            List<Account> accounts = new ArrayList<>(1);
            accounts.add(new Account(accountId, venue));
            final Stock stock = new Stock(venue, symbol);
            List<Stock> stocks = new ArrayList<>(1);
            stocks.add(stock);
            try (final BackOfficeManager backOfficeManager =
                         new BackOfficeManager(accounts, stocks,
                                 useExecutionReceiver, expireOrders,
                                 useQuoteReceiver)) {
                while (!levelManager.isLevelComplete()) {
                    backOfficeManager.gatherStatistics();
                    backOfficeManager.logStatus();
                }
            }
        }
    }
}
