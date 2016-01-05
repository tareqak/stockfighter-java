package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.QuoteResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class QuoteRequest extends StockfighterHttpRequest {
    public QuoteRequest(final CloseableHttpClient httpClient,
                        final String venue, final String stock) {
        super(httpClient, HttpRequestType.GET, BaseUrl.API, "venues/" + venue +
                "/stocks/" + stock + "/quote", false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return QuoteResponse.class;
    }
}
