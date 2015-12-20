package gm.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gm.responses.abstractions.Details;
import gm.responses.abstractions.Flash;
import http.StockfighterHttpResponse;

public class LevelHeartbeatResponse extends StockfighterHttpResponse {
    private final Boolean done;
    private final Integer id;
    private final String state;
    private Details details;
    private Flash flash;
    private String error;

    @JsonCreator
    public LevelHeartbeatResponse(@JsonProperty("ok") final Boolean ok,
                                  @JsonProperty("done") final Boolean done,
                                  @JsonProperty("id") final Integer id,
                                  @JsonProperty("state") final String state) {
        super(ok);
        this.done = done;
        this.id = id;
        this.state = state;
    }

    public Flash getFlash() {
        return flash;
    }

    @SuppressWarnings("unused")
    @JsonProperty("flash")
    public void setFlash(final Flash flash) {
        this.flash = flash;
    }

    @SuppressWarnings("unused")
    @JsonProperty("error")
    public void setError(String error) {
        this.error = error;
    }

    public String getState() {
        return state;
    }

    public Details getDetails() {
        return details;
    }

    @SuppressWarnings("unused")
    @JsonProperty("details")
    public void setDetails(final Details details) {
        this.details = details;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LevelHeartbeatResponse{done=")
                .append(done)
                .append(", id=").append(id)
                .append(", state=").append(state)
                .append(", details=").append(details)
                .append(", flash=").append(flash);
        if (error != null) {
            stringBuilder.append(", error=\"").append(error).append("\"");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
