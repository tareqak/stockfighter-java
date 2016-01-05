package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.AllOrdersResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class AllOrdersRequest extends StockfighterHttpRequest {
    public AllOrdersRequest(final CloseableHttpClient httpClient,
                            final String venue, final String account,
                            final String stock) {
        super(httpClient, HttpRequestType.GET,
                BaseUrl.API, "venues/" + venue + "/accounts/" + account +
                        "/stocks/" + stock + "/orders", true);
    }

    public AllOrdersRequest(final CloseableHttpClient httpClient,
                            final String venue, final String account) {
        super(httpClient, HttpRequestType.GET,
                BaseUrl.API, "venues/" + venue + "/accounts/" + account +
                        "/orders", true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return AllOrdersResponse.class;
    }
}
