package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.CancelOrderResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class CancelOrderRequest extends StockfighterHttpRequest {
    public CancelOrderRequest(final CloseableHttpClient httpClient,
                              final String venue, final String stock,
                              final Integer order) {
        super(httpClient, HttpRequestType.DELETE, BaseUrl.API, "venues/" +
                venue + "/stocks/" + stock + "/orders/" + order, true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return CancelOrderResponse.class;
    }
}
