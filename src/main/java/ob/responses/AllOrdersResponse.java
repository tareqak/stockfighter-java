package ob.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;
import ob.abstractions.Order;

import java.util.List;
import java.util.stream.Collectors;

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
        stringBuilder.append("All Orders Response:\nvenue: ").append(venue);
        if (orders.size() > 0) {
            stringBuilder.append("\nOrders:\n");
            stringBuilder.append(orders.stream().map(Order::toString)
                    .collect(Collectors.joining("\n")));
        }
        return stringBuilder.toString();
    }
}
