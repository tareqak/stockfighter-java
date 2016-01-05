package gm.requests;

import gm.responses.StopLevelResponse;
import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class StopLevelRequest extends StockfighterHttpRequest {
    public StopLevelRequest(final CloseableHttpClient httpClient,
                            final Integer instanceId) {
        super(httpClient, HttpRequestType.POST, BaseUrl.GM, "instances/" +
                instanceId + "/stop", true);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return StopLevelResponse.class;
    }
}
