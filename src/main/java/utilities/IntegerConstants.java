package utilities;

public enum IntegerConstants {
    MILLISECONDS_IN_A_SECOND(1000);

    private final int value;

    IntegerConstants(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
