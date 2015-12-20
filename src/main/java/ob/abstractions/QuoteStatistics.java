package ob.abstractions;

import ob.backoffice.websocket.abstractions.Quote;

public class QuoteStatistics {
    private final QuoteStatistic bid = new QuoteStatistic();
    private final QuoteStatistic ask = new QuoteStatistic();
    private final QuoteStatistic last = new QuoteStatistic();
    private Integer bidSize = null;
    private Integer bidDepth = null;
    private Integer askSize = null;
    private Integer askDepth = null;
    private Integer lastSize = null;

    public QuoteStatistic getBid() {
        return bid;
    }

    public QuoteStatistic getAsk() {
        return ask;
    }

    public QuoteStatistic getLast() {
        return last;
    }

    public Integer getBidSize() {
        return bidSize;
    }

    public Integer getAskSize() {
        return askSize;
    }

    public Integer getLastSize() {
        return lastSize;
    }

    public Integer getBidDepth() {
        return bidDepth;
    }

    public Integer getAskDepth() {
        return askDepth;
    }

    public void processQuote(final Quote quote) {
        bid.setCurrentValue(quote.getBid());
        bidSize = quote.getBidSize();
        bidDepth = quote.getBidDepth();
        ask.setCurrentValue(quote.getAsk());
        askSize = quote.getAskSize();
        askDepth = quote.getAskDepth();
        last.setCurrentValue(quote.getLast());
        lastSize = quote.getLastSize();
    }

    public class QuoteStatistic {
        private Integer currentValue = null;
        private Integer lastNotNull = null;
        private Integer maximum = Integer.MIN_VALUE;
        private Integer minimum = Integer.MAX_VALUE;

        private QuoteStatistic() {
        }

        public Integer getMaximum() {
            return maximum;
        }

        public Integer getMinimum() {
            return minimum;
        }

        public Integer getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(Integer i) {
            currentValue = i;
            if (i != null) {
                lastNotNull = i;
                maximum = Math.max(maximum, i);
                minimum = Math.min(minimum, i);
            }
        }

        public Integer getLastNotNull() {
            return lastNotNull;
        }
    }
}
