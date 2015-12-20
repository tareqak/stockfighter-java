package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.VenueHeartbeatResponse;

public class VenueHeartbeatRequest extends StockfighterHttpRequest {
    public VenueHeartbeatRequest(final String venue) {
        super(HttpRequestType.GET, BaseUrl.API, "venues/" + venue +
                "/heartbeat", false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return VenueHeartbeatResponse.class;
    }
}
