package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.VenueHeartbeatResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class VenueHeartbeatRequest extends StockfighterHttpRequest {
    public VenueHeartbeatRequest(final CloseableHttpClient httpClient,
                                 final String venue) {
        super(httpClient, HttpRequestType.GET, BaseUrl.API, "venues/" + venue +
                "/heartbeat", false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return VenueHeartbeatResponse.class;
    }
}
