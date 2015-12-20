package ob.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;
import ob.abstractions.Order;

import java.util.List;

public class AllOrdersResponse extends StockfighterHttpResponse {
    List<Order> orders;
    private String venue;

    @JsonCreator
    public AllOrdersResponse(@JsonProperty("ok") final Boolean ok,
                             @JsonProperty("venue") final String venue,
                             @JsonProperty("orders")
                             final List<Order> orders) {
        super(ok);
        this.venue = venue;
        this.orders = orders;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nAll Orders Response:\nvenue: ").append(venue);
        if (orders.size() > 0) {
            stringBuilder.append("\nOrders:\n");
            boolean first = true;
            for (final Order order : orders) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(order);
            }
        }
        return stringBuilder.toString();
    }
}
