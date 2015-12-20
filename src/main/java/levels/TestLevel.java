package levels;

import ob.abstractions.Direction;
import ob.abstractions.OrderType;
import ob.backoffice.BackOfficeManager;
import ob.requests.*;

public class TestLevel extends StockfighterLevel {
    @Override
    public void execute() {
        final String account = "EXB123456";
        final String venue = "TESTEX";
        final String symbol = "FOOBAR";
        final boolean useQuoteReceiver = false;
        final boolean useExecutionReceiver = false;
        final boolean expireOrders = true;

        try (final BackOfficeManager backOfficeManager =
                     new BackOfficeManager(account, venue, symbol,
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
                    symbol, account, 0, 100, Direction.BUY, OrderType.MARKET);
            logger.info(newOrderRequest.getResponse().toString());

            final AllOrdersRequest allOrdersRequest = new AllOrdersRequest(venue,
                    account);
            logger.info(allOrdersRequest.getResponse().toString());

            final AllOrdersRequest allOrdersRequest2 = new AllOrdersRequest(venue,
                    account, symbol);
            logger.info(allOrdersRequest2.getResponse().toString());
        }
    }
}
