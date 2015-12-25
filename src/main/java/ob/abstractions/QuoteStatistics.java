package ob.abstractions;

import ob.backoffice.websocket.abstractions.Quote;

public class QuoteStatistics {
    private final QuoteStatistic bid = new QuoteStatistic();
    private final QuoteStatistic ask = new QuoteStatistic();
    private Integer last = null;
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

    public Integer getLast() {
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

    public boolean processQuote(final Quote quote) {
        final Integer bid = quote.getBid();
        final Integer bidSize = quote.getBidSize();
        final Integer bidDepth = quote.getBidDepth();
        final Integer ask = quote.getAsk();
        final Integer askSize = quote.getAskSize();
        final Integer askDepth = quote.getAskDepth();
        final Integer last = quote.getLast();
        final Integer lastSize = quote.getLastSize();

        // Only store and log quote if it is different from before
        if ((bid != null && !bid.equals(this.bid.getCurrentValue())) ||
                (bidSize != null && !bidSize.equals(this.bidSize)) ||
                (bidDepth != null && !bidDepth.equals(this.bidDepth)) ||
                (ask != null && !ask.equals(this.ask.getCurrentValue())) ||
                (askSize != null && !askSize.equals(this.askSize)) ||
                (askDepth != null && !askDepth.equals(this.askDepth)) ||
                (last != null && !last.equals(this.last)) ||
                (lastSize != null && !lastSize.equals(this.lastSize))) {
            this.bid.setCurrentValue(bid);
            this.bidSize = bidSize;
            this.bidDepth = bidDepth;
            this.ask.setCurrentValue(ask);
            this.askSize = askSize;
            this.askDepth = askDepth;
            this.last = last;
            this.lastSize = lastSize;
            return true;
        }
        return false;
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
