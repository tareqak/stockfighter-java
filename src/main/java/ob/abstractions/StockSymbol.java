package ob.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StockSymbol {
    private final String name;
    private final String symbol;

    @JsonCreator
    public StockSymbol(@JsonProperty("name") final String name,
                       @JsonProperty("symbol") final String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol + ":" + name;
    }
}
