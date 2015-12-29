package ob.abstractions;

import ob.backoffice.websocket.abstractions.Quote;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class QuoteStatistics {
    private final QuoteStatistic bid = new QuoteStatistic();
    private final QuoteStatistic ask = new QuoteStatistic();
    private final ReentrantReadWriteLock bidReentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock askReentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock lastReentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private Integer last = null;
    private Integer bidSize = null;
    private Integer bidDepth = null;
    private Integer askSize = null;
    private Integer askDepth = null;
    private Integer lastSize = null;

    public QuoteStatistic getBid() {
        try {
            bidReentrantReadWriteLock.readLock().lock();
            return bid;
        } finally {
            bidReentrantReadWriteLock.readLock().unlock();
        }
    }

    public QuoteStatistic getAsk() {
        try {
            askReentrantReadWriteLock.readLock().lock();
            return ask;
        } finally {
            askReentrantReadWriteLock.readLock().unlock();
        }
    }

    public Integer getLast() {
        try {
            lastReentrantReadWriteLock.readLock().lock();
            return last;
        } finally {
            lastReentrantReadWriteLock.readLock().unlock();
        }
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
            bidReentrantReadWriteLock.writeLock().lock();
            this.bid.setCurrentValue(bid);
            bidReentrantReadWriteLock.writeLock().unlock();
            this.bidSize = bidSize;
            this.bidDepth = bidDepth;

            askReentrantReadWriteLock.writeLock().lock();
            this.ask.setCurrentValue(ask);
            askReentrantReadWriteLock.writeLock().unlock();
            this.askSize = askSize;
            this.askDepth = askDepth;

            lastReentrantReadWriteLock.writeLock().lock();
            this.last = last;
            lastReentrantReadWriteLock.writeLock().unlock();
            this.lastSize = lastSize;

            return true;
        }
        return false;
    }

    public class QuoteStatistic {
        private final DescriptiveStatistics descriptiveStatistics =
                new DescriptiveStatistics();
        private Integer currentValue = null;
        private boolean assigned = false;

        private QuoteStatistic() {
        }

        public DescriptiveStatistics getDescriptiveStatistics() {
            return descriptiveStatistics;
        }

        public Integer getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(Integer i) {
            currentValue = i;
            if (i != null) {
                assigned = true;
                descriptiveStatistics.addValue(i);
            }
        }

        public boolean isAssigned() {
            return assigned;
        }
    }
}
