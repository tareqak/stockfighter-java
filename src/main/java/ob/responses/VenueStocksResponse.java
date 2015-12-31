package ob.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;
import ob.abstractions.StockSymbol;

import java.util.List;
import java.util.stream.Collectors;

public class VenueStocksResponse extends StockfighterHttpResponse {
    private final List<StockSymbol> symbols;

    @JsonCreator
    public VenueStocksResponse(@JsonProperty("ok") final Boolean ok,
                               @JsonProperty("symbols")
                               final List<StockSymbol> symbols) {
        super(ok);
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        if (ok) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("StockSymbol Listing\n");
            stringBuilder.append(symbols.stream().map(StockSymbol::toString)
                    .collect(Collectors.joining("\n")));
            return stringBuilder.toString();
        } else {
            return "Venue stocks response not OK.";
        }
    }
}
