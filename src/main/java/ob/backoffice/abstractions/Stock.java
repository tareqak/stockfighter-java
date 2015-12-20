package ob.backoffice.abstractions;

public class Stock {
    private final String venue;
    private final String symbol;

    public Stock(final String venue, final String symbol) {
        this.venue = venue;
        this.symbol = symbol;
    }

    public String getVenue() {
        return venue;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Stock that = (Stock) o;

        if (!venue.equals(that.venue)) return false;
        return symbol.equals(that.symbol);

    }

    @Override
    public int hashCode() {
        int result = venue.hashCode();
        result = 31 * result + symbol.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", venue, symbol);
    }
}
