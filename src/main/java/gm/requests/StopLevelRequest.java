package gm.requests;

import gm.responses.StopLevelResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;

public class StopLevelRequest extends StockfighterHttpRequest {
    public StopLevelRequest(final Integer instanceId) {
        super(StockfighterHttpRequest.HttpRequestType.POST, BaseUrl.GM,
                "instances/" + instanceId.toString() + "/stop",
                true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return StopLevelResponse.class;
    }
}
