package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.OrderbookResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class OrderbookRequest extends StockfighterHttpRequest {
    public OrderbookRequest(final CloseableHttpClient httpClient,
                            final String venue, final String stock) {
        super(httpClient, HttpRequestType.GET, BaseUrl.API, "venues/" + venue +
                "/stocks/" + stock, false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return OrderbookResponse.class;
    }
}
