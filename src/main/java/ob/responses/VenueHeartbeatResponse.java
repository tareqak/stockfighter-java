package ob.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;

public class VenueHeartbeatResponse extends StockfighterHttpResponse {
    private final String venue;

    @JsonCreator
    public VenueHeartbeatResponse(@JsonProperty("ok") final Boolean ok,
                                  @JsonProperty("venue") final String venue) {
        super(ok);
        this.venue = venue;
    }

    @Override
    public String toString() {
        if (ok) {
            return "Venue " + venue + " is up.";
        } else {
            return "Venue " + venue + " is down.";
        }
    }
}
