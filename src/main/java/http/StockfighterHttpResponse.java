package http;

public abstract class StockfighterHttpResponse {
    protected final Boolean ok;

    public StockfighterHttpResponse(final Boolean ok) {
        this.ok = ok;
    }
}
