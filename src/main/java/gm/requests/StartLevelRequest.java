package gm.requests;

import gm.responses.LevelResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;

public class StartLevelRequest extends StockfighterHttpRequest {
    public StartLevelRequest(final String levelName) {
        super(HttpRequestType.POST, BaseUrl.GM, "levels/" + levelName, true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return LevelResponse.class;
    }
}
