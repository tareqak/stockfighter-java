package gm.requests;

import gm.responses.LevelResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;

public class ResumeLevelRequest extends StockfighterHttpRequest {
    public ResumeLevelRequest(final Integer instanceId) {
        super(HttpRequestType.POST, BaseUrl.GM, "instances/"
                + instanceId.toString() + "/resume", true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return LevelResponse.class;
    }
}
