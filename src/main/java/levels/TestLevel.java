package levels;

import ob.abstractions.Direction;
import ob.abstractions.OrderType;
import ob.backoffice.BackOfficeManager;
import ob.backoffice.abstractions.Accounts;
import ob.backoffice.abstractions.Stocks;
import ob.requests.*;

import java.util.List;

public class TestLevel extends StockfighterLevel {
    @Override
    protected void actuallyPlay() {
        final String accountId = "EXB123456";
        final String venue = "TESTEX";
        final String symbol = "FOOBAR";
        final boolean useQuoteReceiver = true;
        final boolean useExecutionReceiver = true;
        final boolean expireOrders = true;

        Stocks.getStock(venue, symbol);
        Accounts.getAccount(venue, accountId);
        final List<Stocks.Stock> stocks = Stocks.getStocks();
        final List<Accounts.Account> accounts = Accounts.getAccounts();

        try (final BackOfficeManager backOfficeManager =
                     new BackOfficeManager(accounts, stocks, 0,
                             useExecutionReceiver, expireOrders,
                             useQuoteReceiver)) {
            final ApiHeartbeatRequest apiHeartbeatRequest =
                    new ApiHeartbeatRequest();
            logger.info(apiHeartbeatRequest.getResponse().toString());

            final VenueHeartbeatRequest venueHeartbeatRequest =
                    new VenueHeartbeatRequest(venue);
            logger.info(venueHeartbeatRequest.getResponse().toString());

            final VenueStocksRequest venueStocksRequest =
                    new VenueStocksRequest(venue);
            logger.info(venueStocksRequest.getResponse().toString());

            final OrderbookRequest orderbookRequest =
                    new OrderbookRequest(venue, symbol);
            logger.info(orderbookRequest.getResponse().toString());

            final QuoteRequest quoteRequest = new QuoteRequest(venue, symbol);
            logger.info(quoteRequest.getResponse().toString());

            final NewOrderRequest newOrderRequest = new NewOrderRequest(venue,
                    symbol, accountId, 0, 100, Direction.BUY, OrderType.MARKET);
            logger.info(newOrderRequest.getResponse().toString());

            final AllOrdersRequest allOrdersRequest =
                    new AllOrdersRequest(venue, accountId);
            logger.info(allOrdersRequest.getResponse().toString());

            final AllOrdersRequest allOrdersRequest2 =
                    new AllOrdersRequest(venue, accountId, symbol);
            logger.info(allOrdersRequest2.getResponse().toString());
        }
    }
}
