package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.abstractions.Direction;
import ob.abstractions.OrderType;
import ob.responses.NewOrderResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class NewOrderRequest extends StockfighterHttpRequest {
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
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return NewOrderResponse.class;
    }
}
