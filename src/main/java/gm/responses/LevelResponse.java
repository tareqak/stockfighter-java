package gm.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import http.StockfighterHttpResponse;

import java.util.List;
import java.util.Map;

public class LevelResponse extends StockfighterHttpResponse {
    private final String account;
    private final Integer instanceId;
    @SuppressWarnings("unused")
    private final Map<String, String> instructions;
    private final Integer secondsPerTradingDay;
    private final List<String> tickers;
    private final List<String> venues;
    private final Map<String, Integer> balances;
    private String error;

    @JsonCreator
    public LevelResponse(@JsonProperty("ok") final Boolean ok,
                         @JsonProperty("account") final String account,
                         @JsonProperty("instanceId") final Integer instanceId,
                         @JsonProperty("instructions")
                         final Map<String, String> instructions,
                         @JsonProperty("secondsPerTradingDay")
                         final Integer secondsPerTradingDay,
                         @JsonProperty("tickers") final List<String> tickers,
                         @JsonProperty("venues") final List<String> venues,
                         @JsonProperty("balances")
                         final Map<String, Integer> balances) {
        super(ok);
        this.account = account;
        this.instanceId = instanceId;
        this.instructions = instructions;
        this.secondsPerTradingDay = secondsPerTradingDay;
        this.tickers = tickers;
        this.venues = venues;
        this.balances = balances;
    }

    public String getAccount() {
        return account;
    }

    public Integer getInstanceId() {
        return instanceId;
    }

    public List<String> getTickers() {
        return tickers;
    }

    public List<String> getVenues() {
        return venues;
    }

    public Map<String, Integer> getBalances() {
        return balances;
    }

    public String getError() {
        return error;
    }

    @SuppressWarnings("unused")
    @JsonProperty("error")
    public void setError(final String error) {
        this.error = error;
    }

    public Integer getSecondsPerTradingDay() {
        return secondsPerTradingDay;
    }
}
