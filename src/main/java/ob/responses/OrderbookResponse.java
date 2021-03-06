package ob.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;
import ob.abstractions.StockRequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderbookResponse extends StockfighterHttpResponse {
    private final String venue;
    private final String symbol;
    private final List<StockRequest> bids;
    private final List<StockRequest> asks;
    private final ZonedDateTime timestamp;

    @JsonCreator
    public OrderbookResponse(@JsonProperty("ok") final Boolean ok,
                             @JsonProperty("venue") final String venue,
                             @JsonProperty("symbol") final String symbol,
                             @JsonProperty("bids")
                             final List<StockRequest> bids,
                             @JsonProperty("asks")
                             final List<StockRequest> asks,
                             @JsonProperty("ts") final String timestamp) {
        super(ok);
        this.venue = venue;
        this.symbol = symbol;
        this.bids = bids;
        this.asks = asks;
        this.timestamp = ZonedDateTime.parse(timestamp);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Orderbook :: ").append(venue)
                .append(":").append(symbol)
                .append(" @ ").append(timestamp.toString());
        if (bids != null) {
            stringBuilder.append("\nBids:\n");
            stringBuilder.append(bids.stream().map(StockRequest::toString)
                    .collect(Collectors.joining("\n")));
        }
        if (asks != null) {
            stringBuilder.append("\nAsks:\n");
            stringBuilder.append(asks.stream().map(StockRequest::toString)
                    .collect(Collectors.joining("\n")));
        }
        return stringBuilder.toString();
    }

    public List<StockRequest> getBids() {
        return bids;
    }

    public List<StockRequest> getAsks() {
        return asks;
    }
}
