package ob.abstractions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Direction {
    BUY("buy"), SELL("sell");

    private final String text;

    Direction(String text) {
        this.text = text;
    }

    public static Direction getDirection(final String text) {
        final List<Direction> collect = Arrays.stream(Direction.values())
                .parallel().filter(direction -> direction.text.equals(text))
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            return collect.get(0);
        }
        throw new RuntimeException("Invalid direction: " + text);
    }

    public String getText() {
        return text;
    }
}
