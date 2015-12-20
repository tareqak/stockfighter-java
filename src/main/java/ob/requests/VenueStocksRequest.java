package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.VenueStocksResponse;

public class VenueStocksRequest extends StockfighterHttpRequest {
    public VenueStocksRequest(final String venue) {
        super(HttpRequestType.GET, BaseUrl.API, "venues/" + venue +
                "/stocks", false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return VenueStocksResponse.class;
    }
}
