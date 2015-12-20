package ob.abstractions;

public enum OrderType {
    LIMIT("limit"), MARKET("market"), FILL_OR_KILL("fill-or-kill"),
    IMMEDIATE_OR_CANCEL("immediate-or-cancel");

    private final String text;

    OrderType(String text) {
        this.text = text;
    }

    public static OrderType getOrderType(final String text) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.text.equals(text)) {
                return orderType;
            }
        }
        throw new RuntimeException("Invalid order type: " + text);
    }

    public String getText() {
        return text;
    }
}
