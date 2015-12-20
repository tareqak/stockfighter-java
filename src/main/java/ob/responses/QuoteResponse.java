package ob.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;

import java.time.ZonedDateTime;

public class QuoteResponse extends StockfighterHttpResponse {
    private final String symbol;
    private final String venue;
    private final Integer bid;
    private final Integer ask;
    private final Integer bidSize;
    private final Integer askSize;
    private final Integer bidDepth;
    private final Integer askDepth;
    private Integer last;   // optional
    private Integer lastSize;   // optional
    private ZonedDateTime lastTrade;    // optional
    private ZonedDateTime quoteTime;

    @JsonCreator
    public QuoteResponse(@JsonProperty("ok") final Boolean ok,
                         @JsonProperty("symbol") final String symbol,
                         @JsonProperty("venue") final String venue,
                         @JsonProperty("bid") final Integer bid,
                         @JsonProperty("ask") final Integer ask,
                         @JsonProperty("bidSize") final Integer bidSize,
                         @JsonProperty("askSize") final Integer askSize,
                         @JsonProperty("bidDepth") final Integer bidDepth,
                         @JsonProperty("askDepth") final Integer askDepth) {
        super(ok);
        this.symbol = symbol;
        this.venue = venue;
        this.bid = bid;
        this.ask = ask;
        this.bidSize = bidSize;
        this.askSize = askSize;
        this.bidDepth = bidDepth;
        this.askDepth = askDepth;
    }

    @SuppressWarnings("unused")
    @JsonProperty("lastTrade")
    public void setLastTrade(final String lastTrade) {
        this.lastTrade = ZonedDateTime.parse(lastTrade);
    }

    @Override
    public String toString() {
        if (ok) {
            StringBuilder stringBuilder = new StringBuilder()
                    .append("\nTickerTape :: ").append(venue).append(":")
                    .append(symbol);
            if (quoteTime != null) {
                stringBuilder.append(" @ ").append(quoteTime.toString());
            }
            stringBuilder.append("\nbid: ").append(bid)
                    .append(" bidSize: ").append(bidSize)
                    .append(" bidDepth: ").append(bidDepth);
            stringBuilder.append("\nask: ").append(ask)
                    .append(" askSize: ").append(askSize)
                    .append(" askDepth: ").append(askDepth);
            stringBuilder.append("\nlast: ").append(last).append(" lastSize: ")
                    .append(lastSize).append(" lastTrade: ")
                    .append(lastTrade);
            return stringBuilder.toString();
        } else {
            return "TickerTape response failed.";
        }
    }

    public Integer getBid() {
        return bid;
    }

    public Integer getAsk() {
        return ask;
    }

    public Integer getBidSize() {
        return bidSize;
    }

    public Integer getAskSize() {
        return askSize;
    }

    public Integer getBidDepth() {
        return bidDepth;
    }

    public Integer getAskDepth() {
        return askDepth;
    }

    public Integer getLast() {
        return last;
    }

    @SuppressWarnings("unused")
    @JsonProperty("last")
    public void setLast(final Integer last) {
        this.last = last;
    }

    public Integer getLastSize() {
        return lastSize;
    }

    @SuppressWarnings("unused")
    @JsonProperty("lastSize")
    public void setLastSize(final Integer lastSize) {
        this.lastSize = lastSize;
    }

    public ZonedDateTime getQuoteTime() {
        return quoteTime;
    }

    @SuppressWarnings("unused")
    @JsonProperty("quoteTime")
    public void setQuoteTime(final String quoteTime) {
        this.quoteTime = ZonedDateTime.parse(quoteTime);
    }
}
