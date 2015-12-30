package gm.responses.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Details {
    private final Integer endOfTheWorldDay;
    private final Integer tradingDay;
    private Integer executionTime;

    @JsonCreator
    public Details(@JsonProperty("endOfTheWorldDay")
                       final Integer endOfTheWorldDay,
                   @JsonProperty("tradingDay") final Integer tradingDay) {
        this.endOfTheWorldDay = endOfTheWorldDay;
        this.tradingDay = tradingDay;
    }

    public Integer getEndOfTheWorldDay() {
        return endOfTheWorldDay;
    }

    public Integer getTradingDay() {
        return tradingDay;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    @JsonProperty("executionTime")
    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("endOfTheWorldDay=")
                .append(endOfTheWorldDay)
                .append(", tradingDay=").append(tradingDay);
        if (executionTime != null) {
            stringBuilder.append(", executionTime=").append(executionTime);
        }
        return stringBuilder.toString();
    }
}
