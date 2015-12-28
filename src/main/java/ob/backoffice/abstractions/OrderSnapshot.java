package ob.backoffice.abstractions;

public class OrderSnapshot {
    private final Integer totalFilled;
    private final Integer sharePriceValue;

    public OrderSnapshot(final Integer totalFilled,
                         final Integer sharePriceValue) {
        this.totalFilled = totalFilled;
        this.sharePriceValue = sharePriceValue;
    }

    public Integer getTotalFilled() {
        return totalFilled;
    }

    public Integer getSharePriceValue() {
        return sharePriceValue;
    }
}
