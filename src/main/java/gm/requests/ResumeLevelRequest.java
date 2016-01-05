package gm.requests;

import gm.responses.LevelResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class ResumeLevelRequest extends StockfighterHttpRequest {
    public ResumeLevelRequest(final CloseableHttpClient httpClient,
                              final Integer instanceId) {
        super(httpClient, HttpRequestType.POST, BaseUrl.GM, "instances/"
                + instanceId + "/resume", true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return LevelResponse.class;
    }
}
