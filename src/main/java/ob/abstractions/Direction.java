package ob.abstractions;

public enum Direction {
    BUY("buy"), SELL("sell");

    private final String text;

    Direction(String text) {
        this.text = text;
    }

    public static Direction getDirection(final String text) {
        for (Direction direction : Direction.values()) {
            if (direction.text.equals(text)) {
                return direction;
            }
        }
        throw new RuntimeException("Invalid direction: " + text);
    }

    public String getText() {
        return text;
    }
}
