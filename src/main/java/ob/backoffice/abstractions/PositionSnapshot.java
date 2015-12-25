package ob.backoffice.abstractions;

public class PositionSnapshot {
    private final int position;
    private final int sharePriceValue;

    public PositionSnapshot(final int position, final int sharePriceValue) {
        this.position = position;
        this.sharePriceValue = sharePriceValue;
    }

    public int getPosition() {
        return position;
    }

    public int getSharePriceValue() {
        return sharePriceValue;
    }

    public int getAverageSharePrice() {
        if (position == 0) {
            return 0;
        }
        return Math.abs(sharePriceValue / position);
    }
}
