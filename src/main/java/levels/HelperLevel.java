package levels;

import gm.LevelManager;
import gm.responses.LevelResponse;
import ob.backoffice.BackOfficeManager;

public class HelperLevel extends StockfighterLevel {
    @Override
    public void execute() {
        final boolean useExecutionReceiver = true;
        final boolean expireOrders = false;
        final boolean useQuoteReceiver = true;
        try (LevelManager levelManager = new LevelManager("sell_side")) {
            final LevelResponse levelResponse = levelManager.getLevelResponse();
            final String venue = levelResponse.getVenues().get(0);
            final String stock = levelResponse.getTickers().get(0);
            final String account = levelResponse.getAccount();
            try (final BackOfficeManager backOfficeManager =
                         new BackOfficeManager(account, venue, stock,
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
