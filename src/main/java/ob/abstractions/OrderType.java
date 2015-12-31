package ob.abstractions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OrderType {
    LIMIT("limit"), MARKET("market"), FILL_OR_KILL("fill-or-kill"),
    IMMEDIATE_OR_CANCEL("immediate-or-cancel");

    private final String text;

    OrderType(String text) {
        this.text = text;
    }

    public static OrderType getOrderType(final String text) {
        final List<OrderType> collect = Arrays.stream(OrderType.values())
                .parallel().filter(orderType -> orderType.text.equals(text))
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            return collect.get(0);
        }
        throw new RuntimeException("Invalid order type: " + text);
    }

    public String getText() {
        return text;
    }
}
