package ob.backoffice.websocket.abstractions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TickerTape {
    private final Boolean ok;
    private Quote quote;
    private String error;

    @JsonCreator
    public TickerTape(@JsonProperty("ok") Boolean ok) {
        this.ok = ok;
    }

    public Boolean getOk() {
        return ok;
    }

    public Quote getQuote() {
        return quote;
    }

    @JsonProperty("quote")
    public void setQuote(final Quote quote) {
        this.quote = quote;
    }

    public String getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(final String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return String.format("Tickertape{ok:%s,quote:%s,error:%s}", ok, quote,
                error);
    }
}
