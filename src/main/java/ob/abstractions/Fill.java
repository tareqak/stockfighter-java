package ob.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class Fill {
    private final Integer price;
    private final Integer quantity;
    private final ZonedDateTime timestamp;

    @JsonCreator
    public Fill(@JsonProperty("price") final Integer price,
                @JsonProperty("qty") final Integer quantity,
                @JsonProperty("ts") final String timestamp) {
        this.price = price;
        this.quantity = quantity;
        this.timestamp = ZonedDateTime.parse(timestamp);
    }

    @Override
    public String toString() {
        return "price: " + price + " qty: " + quantity + " ts: " + timestamp;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
