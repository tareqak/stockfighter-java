package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.AllOrdersResponse;

public class AllOrdersRequest extends StockfighterHttpRequest {
    public AllOrdersRequest(final String venue, final String account,
                            final String stock) {
        super(HttpRequestType.GET, BaseUrl.API,
                "venues/" + venue + "/accounts/" + account + "/stocks/" + stock
                        + "/orders", true);
    }

    public AllOrdersRequest(final String venue, final String account) {
        super(HttpRequestType.GET, BaseUrl.API,
                "venues/" + venue + "/accounts/" + account + "/orders", true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return AllOrdersResponse.class;
    }
}
