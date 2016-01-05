package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.VenueStocksResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class VenueStocksRequest extends StockfighterHttpRequest {
    public VenueStocksRequest(final CloseableHttpClient httpClient,
                              final String venue) {
        super(httpClient, HttpRequestType.GET, BaseUrl.API, "venues/" + venue +
                "/stocks", false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return VenueStocksResponse.class;
    }
}
