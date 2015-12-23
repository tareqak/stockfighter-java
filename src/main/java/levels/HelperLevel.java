package levels;

import gm.LevelManager;
import gm.responses.LevelResponse;
import ob.backoffice.BackOfficeManager;
import ob.backoffice.abstractions.Account;

import java.util.ArrayList;
import java.util.List;

public class HelperLevel extends StockfighterLevel {
    @Override
    protected void actuallyPlay() {
        final boolean useExecutionReceiver = true;
        final boolean expireOrders = false;
        final boolean useQuoteReceiver = true;
        try (LevelManager levelManager = new LevelManager("sell_side")) {
            final LevelResponse levelResponse = levelManager.getLevelResponse();
            final String venue = levelResponse.getVenues().get(0);
            final String accountId = levelResponse.getAccount();
            final Account account = new Account(accountId, venue);
            List<Account> accounts = new ArrayList<>(1);
            accounts.add(account);
            try (final BackOfficeManager backOfficeManager =
                         new BackOfficeManager(accounts, useExecutionReceiver,
                                 expireOrders, useQuoteReceiver)) {
                while (!levelManager.isLevelComplete()) {
                    backOfficeManager.gatherStatistics();
                    backOfficeManager.logStatus();
                }
            }
        }
    }
}
