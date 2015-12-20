package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.CancelOrderResponse;

public class CancelOrderRequest extends StockfighterHttpRequest {
    public CancelOrderRequest(final String venue, final String stock,
                              final Integer order) {
        super(HttpRequestType.DELETE, BaseUrl.API, "venues/" + venue +
                "/stocks/" + stock + "/orders/" + order, true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return CancelOrderResponse.class;
    }
}
