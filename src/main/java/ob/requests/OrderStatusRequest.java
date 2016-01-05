package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.OrderStatusResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class OrderStatusRequest extends StockfighterHttpRequest {
    public OrderStatusRequest(final CloseableHttpClient httpClient,
                              final String venue, final String stock,
                              final Integer id) {
        super(httpClient, HttpRequestType.GET, BaseUrl.API, "venues/" + venue +
                "/stocks/" + stock + "/orders/" + id.toString(), true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return OrderStatusResponse.class;
    }
}
