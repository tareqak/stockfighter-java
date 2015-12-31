package ob.backoffice.websocket.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ob.abstractions.Order;
import ob.backoffice.abstractions.Accounts;
import ob.backoffice.abstractions.Stocks;

import java.time.ZonedDateTime;

public class Execution {
    private final Boolean ok;
    private final Accounts.Account account;
    private final Stocks.Stock stock;
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
                     @JsonProperty("incomingComplete") Boolean incomingComplete) {
        this.ok = ok;
        this.account = Accounts.getAccount(venue, accountId);
        this.stock = Stocks.getStock(venue, symbol);
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

    public Accounts.Account getAccount() {
        return account;
    }

    public Stocks.Stock getStock() {
        return stock;
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

    @Override
    public String toString() {
        return "Execution{" +
                "ok=" + ok +
                ", account=\"" + account.getId() + '\"' +
                ", stock=" + stock +
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
