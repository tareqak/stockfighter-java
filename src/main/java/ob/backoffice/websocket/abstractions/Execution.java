package ob.backoffice.websocket.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ob.abstractions.Order;

import java.time.ZonedDateTime;

public class Execution {
    private final Boolean ok;
    private final String venue;
    private final String accountId;
    private final String symbol;
    private final Order order;
    private final Integer standingId;
    private final Integer incomingId;
    private final Integer price;
    private final Integer filled;
    private final ZonedDateTime filledAt;
    private final Boolean standingComplete;
    private final Boolean incomingComplete;

    @JsonCreator
    public Execution(@JsonProperty("ok") Boolean ok,
                     @JsonProperty("account") String accountId,
                     @JsonProperty("venue") String venue,
                     @JsonProperty("symbol") String symbol,
                     @JsonProperty("order") Order order,
                     @JsonProperty("standingId") Integer standingId,
                     @JsonProperty("incomingId") Integer incomingId,
                     @JsonProperty("price") Integer price,
                     @JsonProperty("filled") Integer filled,
                     @JsonProperty("filledAt") String filledAt,
                     @JsonProperty("standingComplete") Boolean standingComplete,
                     @JsonProperty("incomingComplete") Boolean incomingComplete
    ) {
        this.ok = ok;
        this.venue = venue;
        this.accountId = accountId;
        this.symbol = symbol;
        this.order = order;
        this.standingId = standingId;
        this.incomingId = incomingId;
        this.price = price;
        this.filled = filled;
        this.filledAt = ZonedDateTime.parse(filledAt);
        this.standingComplete = standingComplete;
        this.incomingComplete = incomingComplete;
    }

    public Boolean getOk() {
        return ok;
    }

    public Order getOrder() {
        return order;
    }

    public Integer getStandingId() {
        return standingId;
    }

    public Integer getIncomingId() {
        return incomingId;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getFilled() {
        return filled;
    }

    public ZonedDateTime getFilledAt() {
        return filledAt;
    }

    public Boolean getStandingComplete() {
        return standingComplete;
    }

    public Boolean getIncomingComplete() {
        return incomingComplete;
    }

    public String getVenue() {
        return venue;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "ok=" + ok +
                ", venue='" + venue + '\'' +
                ", accountId='" + accountId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", order=" + order +
                ", standingId=" + standingId +
                ", incomingId=" + incomingId +
                ", price=" + price +
                ", filled=" + filled +
                ", filledAt=" + filledAt +
                ", standingComplete=" + standingComplete +
                ", incomingComplete=" + incomingComplete +
                '}';
    }
}
