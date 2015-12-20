package gm.requests;

import gm.responses.LevelResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;

public class RestartLevelRequest extends StockfighterHttpRequest {
    public RestartLevelRequest(final Integer instanceId) {
        super(HttpRequestType.POST, BaseUrl.GM, "instances/"
                + instanceId.toString() + "/restart", true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return LevelResponse.class;
    }
}
