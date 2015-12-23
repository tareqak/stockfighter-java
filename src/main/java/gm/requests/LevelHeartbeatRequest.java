package gm.requests;

import gm.responses.LevelHeartbeatResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;

public class LevelHeartbeatRequest extends StockfighterHttpRequest {
    public LevelHeartbeatRequest(final Integer instanceId) {
        super(HttpRequestType.GET, BaseUrl.GM, "instances/" + instanceId, true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return LevelHeartbeatResponse.class;
    }
}
