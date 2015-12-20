package ob.backoffice.websocket.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class Quote {
    private final String symbol;
    private final String venue;
    private final Integer bid;
    private final Integer ask;
    private final Integer bidSize;
    private final Integer askSize;
    private final Integer bidDepth;
    private final Integer askDepth;
    private final ZonedDateTime quoteTime;
    private Integer last;
    private Integer lastSize;
    private ZonedDateTime lastTrade;

    @JsonCreator
    public Quote(@JsonProperty("symbol") String symbol,
                 @JsonProperty("venue") String venue,
                 @JsonProperty("bid") Integer bid,
                 @JsonProperty("ask") Integer ask,
                 @JsonProperty("bidSize") Integer bidSize,
                 @JsonProperty("askSize") Integer askSize,
                 @JsonProperty("bidDepth") Integer bidDepth,
                 @JsonProperty("askDepth") Integer askDepth,
                 @JsonProperty("quoteTime") String quoteTime) {
        this.symbol = symbol;
        this.venue = venue;
        this.bid = bid;
        this.ask = ask;
        this.bidSize = bidSize;
        this.askSize = askSize;
        this.bidDepth = bidDepth;
        this.askDepth = askDepth;
        this.quoteTime = ZonedDateTime.parse(quoteTime);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getVenue() {
        return venue;
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

    @JsonProperty("last")
    public void setLast(Integer last) {
        this.last = last;
    }

    public Integer getLastSize() {
        return lastSize;
    }

    @JsonProperty("lastSize")
    public void setLastSize(Integer lastSize) {
        this.lastSize = lastSize;
    }

    public ZonedDateTime getLastTrade() {
        return lastTrade;
    }

    @JsonProperty("lastTrade")
    public void setLastTrade(String lastTrade) {
        this.lastTrade = ZonedDateTime.parse(lastTrade);
    }

    public ZonedDateTime getQuoteTime() {
        return quoteTime;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(venue)
                .append(":").append(symbol)
                .append(" bid: ").append(bid)
                .append(" bidSize: ").append(bidSize)
                .append(" bidDepth: ").append(bidDepth)
                .append(" ask: ").append(ask)
                .append(" askSize: ").append(askSize)
                .append(" askDepth: ").append(askDepth)
                .append(" last: ").append(last);
        return stringBuilder.toString();
    }
}
