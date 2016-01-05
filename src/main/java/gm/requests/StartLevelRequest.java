package gm.requests;

import gm.responses.LevelResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class StartLevelRequest extends StockfighterHttpRequest {
    public StartLevelRequest(final CloseableHttpClient httpClient,
                             final String levelName) {
        super(httpClient, HttpRequestType.POST, BaseUrl.GM, "levels/" +
                levelName, true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return LevelResponse.class;
    }
}
