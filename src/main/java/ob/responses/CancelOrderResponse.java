package ob.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;
import ob.abstractions.Fill;
import ob.abstractions.Order;

import java.util.List;

public class CancelOrderResponse extends StockfighterHttpResponse {
    private final Order order;

    public CancelOrderResponse(@JsonProperty("ok") Boolean ok,
                               @JsonProperty("symbol") String symbol,
                               @JsonProperty("venue") String venue,
                               @JsonProperty("direction") String direction,
                               @JsonProperty("originalQty") Integer
                                       originalQuantity,
                               @JsonProperty("qty") Integer quantity,
                               @JsonProperty("price") Integer price,
                               @JsonProperty("orderType") String orderType,
                               @JsonProperty("id") Integer id,
                               @JsonProperty("account") String account,
                               @JsonProperty("ts") String timestamp,
                               @JsonProperty("fills") List<Fill> fills,
                               @JsonProperty("totalFilled") Integer totalFilled,
                               @JsonProperty("open") Boolean open) {
        super(ok);
        this.order = new Order(symbol, venue, direction, originalQuantity,
                quantity, price, orderType, id, account,
                timestamp, fills, totalFilled, open);
    }

    @Override
    public String toString() {
        if (ok) {
            return "Cancel Order Response: " + order;
        } else {
            return "Error occurred in CancelOrderResponse.";
        }
    }
}
