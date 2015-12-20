package ob.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;

public class ApiHeartbeatResponse extends StockfighterHttpResponse {
    private final String error;

    @JsonCreator
    public ApiHeartbeatResponse(@JsonProperty("ok") final Boolean ok,
                                @JsonProperty("error") final String error) {
        super(ok);
        this.error = error;
    }

    @Override
    public String toString() {
        if (ok) {
            return "Stockfighter API is up.";
        } else {
            return "Stockfighter API error: " + error;
        }
    }
}
