package ob.backoffice.abstractions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum Stocks {
    INSTANCE;

    private final Map<String, Map<String, Stock>> stocks = new HashMap<>();
    private final List<Stock> stockList = new LinkedList<>();

    public static Stock getStock(final String venue, final String symbol) {
        if (INSTANCE.stocks.containsKey(venue)) {
            final Map<String, Stock> stockMap = INSTANCE.stocks.get(venue);
            if (stockMap.containsKey(symbol)) {
                return stockMap.get(symbol);
            } else {
                final Stock stock = new Stock(venue, symbol);
                stockMap.put(symbol, stock);
                INSTANCE.stockList.add(stock);
                return stock;
            }
        } else {
            final Stock stock = new Stock(venue, symbol);
            final Map<String, Stock> stockMap = new HashMap<>();
            stockMap.put(symbol, stock);
            INSTANCE.stocks.put(venue, stockMap);
            INSTANCE.stockList.add(stock);
            return stock;
        }
    }

    public static List<Stock> getStocks() {
        return INSTANCE.stockList;
    }

    public static class Stock {
        private final String venue;
        private final String symbol;

        private Stock(final String venue, final String symbol) {
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
}
