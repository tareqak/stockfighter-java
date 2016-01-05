package ob.requests;

import http.StockfighterHttpRequest;
import http.StockfighterHttpResponse;
import ob.responses.ApiHeartbeatResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class ApiHeartbeatRequest extends StockfighterHttpRequest {
    public ApiHeartbeatRequest(final CloseableHttpClient httpClient) {
        super(httpClient, HttpRequestType.GET, BaseUrl.API, "heartbeat", false);
    }

    @Override
    protected Class<? extends StockfighterHttpResponse> getResponseClass() {
        return ApiHeartbeatResponse.class;
    }
}
