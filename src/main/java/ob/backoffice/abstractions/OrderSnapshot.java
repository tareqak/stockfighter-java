package ob.backoffice.abstractions;

public class OrderSnapshot {
    private final int totalFilled;
    private final int sharePriceValue;

    public OrderSnapshot(final int totalFilled, final int sharePriceValue) {
        this.totalFilled = totalFilled;
        this.sharePriceValue = sharePriceValue;
    }

    public int getTotalFilled() {
        return totalFilled;
    }

    public int getSharePriceValue() {
        return sharePriceValue;
    }
}
