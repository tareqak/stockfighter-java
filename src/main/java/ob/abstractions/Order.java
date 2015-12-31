package ob.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ob.backoffice.abstractions.Accounts;
import ob.backoffice.abstractions.Stocks;

import java.time.ZonedDateTime;
import java.util.List;

public class Order {
    private final Stocks.Stock stock;
    private final Direction direction;
    private final Integer originalQuantity;
    private final Integer quantity;
    private final Integer price;
    private final OrderType orderType;
    private final Integer id;
    private final Accounts.Account account;
    private final ZonedDateTime timestamp;
    private final List<Fill> fills;
    private final Integer totalFilled;
    private final Boolean open;
    private Boolean ok;

    @JsonCreator
    public Order(@JsonProperty("symbol") final String symbol,
                 @JsonProperty("venue") final String venue,
                 @JsonProperty("direction") final String direction,
                 @JsonProperty("originalQty") final Integer originalQuantity,
                 @JsonProperty("qty") final Integer quantity,
                 @JsonProperty("price") final Integer price,
                 @JsonProperty("orderType") final String orderType,
                 @JsonProperty("id") final Integer id,
                 @JsonProperty("account") final String accountId,
                 @JsonProperty("ts") final String timestamp,
                 @JsonProperty("fills") final List<Fill> fills,
                 @JsonProperty("totalFilled") final Integer totalFilled,
                 @JsonProperty("open") final Boolean open) {
        this.stock = Stocks.getStock(venue, symbol);
        this.direction = Direction.getDirection(direction);
        this.originalQuantity = originalQuantity;
        this.quantity = quantity;
        this.price = price;
        this.orderType = OrderType.getOrderType(orderType);
        this.id = id;
        this.account = Accounts.getAccount(venue, accountId);
        this.timestamp = ZonedDateTime.parse(timestamp);
        this.fills = fills;
        this.totalFilled = totalFilled;
        this.open = open;
    }

    @Override
    public String toString() {
        return "Order{" +
                "ok=" + ok +
                ", stock=" + stock +
                ", direction=" + direction +
                ", originalQuantity=" + originalQuantity +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderType=" + orderType +
                ", id=" + id +
                ", account=\"" + account.getId() + '\"' +
                ", timestamp=" + timestamp +
                ", fills=" + fills +
                ", totalFilled=" + totalFilled +
                ", open=" + open +
                '}';
    }

    public Stocks.Stock getStock() {
        return stock;
    }

    public Direction getDirection() {
        return direction;
    }

    public Integer getOriginalQuantity() {
        return originalQuantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public Integer getId() {
        return id;
    }

    public Accounts.Account getAccount() {
        return account;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public List<Fill> getFills() {
        return fills;
    }

    public Integer getTotalFilled() {
        return totalFilled;
    }

    public Boolean getOpen() {
        return open;
    }

    public Boolean getOk() {
        return ok;
    }

    @JsonProperty("ok")
    public void setOk(final Boolean ok) {
        this.ok = ok;
    }
}
