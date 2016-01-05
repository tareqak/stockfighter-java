package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.abstractions.Direction;
import ob.abstractions.OrderType;
import ob.backoffice.abstractions.Accounts;
import ob.backoffice.abstractions.OrderStatus;
import ob.backoffice.abstractions.Stocks;
import ob.responses.NewOrderResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class NewOrderRequest extends StockfighterHttpRequest {
    private final OrderStatus orderStatus;

    public NewOrderRequest(final CloseableHttpClient httpClient,
                           final String venue, final String stock,
                           final String account, final Integer price,
                           final Integer quantity, final Direction direction,
                           final OrderType orderType) {
        super(httpClient, HttpRequestType.POST, BaseUrl.API, "venues/" + venue +
                "/stocks/" + stock + "/orders", true);
        addParameter("account", account);
        addParameter("qty", quantity);
        addParameter("direction", direction.getText());
        addParameter("orderType", orderType.getText());
        if (orderType != OrderType.MARKET) {
            addParameter("price", price);
        }
        this.orderStatus = new OrderStatus(Stocks.getStock(venue, stock),
                Accounts.getAccount(venue, account), direction, orderType,
                price, quantity);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return NewOrderResponse.class;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
