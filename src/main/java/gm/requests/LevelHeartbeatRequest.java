package gm.requests;

import gm.responses.LevelHeartbeatResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class LevelHeartbeatRequest extends StockfighterHttpRequest {
    public LevelHeartbeatRequest(final CloseableHttpClient httpClient,
                                 final Integer instanceId) {
        super(httpClient, HttpRequestType.GET, BaseUrl.GM, "instances/" +
                instanceId, true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return LevelHeartbeatResponse.class;
    }
}
