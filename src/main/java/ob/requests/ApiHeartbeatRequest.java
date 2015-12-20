package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.ApiHeartbeatResponse;

public class ApiHeartbeatRequest extends StockfighterHttpRequest {
    public ApiHeartbeatRequest() {
        super(HttpRequestType.GET, BaseUrl.API, "heartbeat", false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return ApiHeartbeatResponse.class;
    }
}
