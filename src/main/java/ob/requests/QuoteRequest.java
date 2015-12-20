package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.QuoteResponse;

public class QuoteRequest extends StockfighterHttpRequest {
    public QuoteRequest(final String venue, final String stock) {
        super(HttpRequestType.GET, BaseUrl.API, "venues/" + venue + "/stocks/"
                + stock + "/quote", false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return QuoteResponse.class;
    }
}
