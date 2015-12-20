package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.OrderbookResponse;

public class OrderbookRequest extends StockfighterHttpRequest {
    public OrderbookRequest(final String venue, final String stock) {
        super(HttpRequestType.GET, BaseUrl.API, "venues/" + venue + "/stocks/"
                + stock, false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return OrderbookResponse.class;
    }
}
