package ob.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StockRequest {
    private final Integer price;
    private final Integer quantity;
    private final Boolean isBuy;

    @JsonCreator
    public StockRequest(@JsonProperty("price") final Integer price,
                        @JsonProperty("qty") final Integer quantity,
                        @JsonProperty("isBuy") final Boolean isBuy) {
        this.price = price;
        this.quantity = quantity;
        this.isBuy = isBuy;
    }

    @Override
    public String toString() {
        return "price: " + price + " qty: " + quantity + " isBuy: " + isBuy;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
