package levels;

import http.StockfighterHttpResponse;
import ob.abstractions.Direction;
import ob.abstractions.OrderType;
import ob.backoffice.BackOfficeManager;
import ob.backoffice.abstractions.Accounts;
import ob.backoffice.abstractions.Stocks;
import ob.requests.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class TestEx {
    private static final Logger logger = LoggerFactory.getLogger(TestEx.class);

    private static void logResponse(final StockfighterHttpResponse response) {
        if (response != null) {
            logger.info(response.toString());
        }
    }

    public void play() {
        final PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10);
        try (final CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager).build()) {
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
                        new ApiHeartbeatRequest(httpClient);
                logResponse(apiHeartbeatRequest.getResponse());

                final VenueHeartbeatRequest venueHeartbeatRequest =
                        new VenueHeartbeatRequest(httpClient, venue);
                logResponse(venueHeartbeatRequest.getResponse());

                final VenueStocksRequest venueStocksRequest =
                        new VenueStocksRequest(httpClient, venue);
                logResponse(venueStocksRequest.getResponse());

                final OrderbookRequest orderbookRequest =
                        new OrderbookRequest(httpClient, venue, symbol);
                logResponse(orderbookRequest.getResponse());

                final QuoteRequest quoteRequest =
                        new QuoteRequest(httpClient, venue, symbol);
                logResponse(quoteRequest.getResponse());

                final NewOrderRequest newOrderRequest =
                        new NewOrderRequest(httpClient, venue, symbol,
                                accountId, 0, 100, Direction.BUY,
                                OrderType.MARKET);
                logResponse(newOrderRequest.getResponse());

                final AllOrdersRequest allOrdersRequest =
                        new AllOrdersRequest(httpClient, venue, accountId);
                logResponse(allOrdersRequest.getResponse());

                final AllOrdersRequest allOrdersRequest2 =
                        new AllOrdersRequest(httpClient, venue, accountId,
                                symbol);
                logResponse(allOrdersRequest2.getResponse());
            }
        } catch (IOException e) {
            logger.error("Error.", e);
        }
    }
}
