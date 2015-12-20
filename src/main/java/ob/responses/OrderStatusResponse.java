package ob.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;
import ob.abstractions.Fill;
import ob.abstractions.Order;

import java.util.List;

public class OrderStatusResponse extends StockfighterHttpResponse {
    private final Order order;

    @JsonCreator
    public OrderStatusResponse(@JsonProperty("ok") final Boolean ok,
                               @JsonProperty("symbol") final String symbol,
                               @JsonProperty("venue") final String venue,
                               @JsonProperty("direction") final String direction,
                               @JsonProperty("originalQty")
                               final Integer originalQuantity,
                               @JsonProperty("qty") final Integer quantity,
                               @JsonProperty("price") final Integer price,
                               @JsonProperty("orderType")
                               final String orderType,
                               @JsonProperty("id") final Integer id,
                               @JsonProperty("account") final String account,
                               @JsonProperty("ts") final String timestamp,
                               @JsonProperty("fills") final List<Fill> fills,
                               @JsonProperty("totalFilled")
                               final Integer totalFilled,
                               @JsonProperty("open") final Boolean open) {
        super(ok);
        this.order = new Order(symbol, venue, direction, originalQuantity,
                quantity, price, orderType, id, account, timestamp, fills,
                totalFilled, open);
    }

    @Override
    public String toString() {
        if (ok) {
            return order.toString();
        } else {
            return "Order status response failed.";
        }
    }
}
