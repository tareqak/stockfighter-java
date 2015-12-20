package gm.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;

public class StopLevelResponse extends StockfighterHttpResponse {
    private final String error;

    @JsonCreator
    public StopLevelResponse(@JsonProperty("ok") final Boolean ok,
                             @JsonProperty("error") final String error) {
        super(ok);
        this.error = error;
    }

    @Override
    public String toString() {
        if (ok) {
            return "Successfully stopped level.";
        } else {
            return "Error: " + error;
        }
    }
}
