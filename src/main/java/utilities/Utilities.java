package utilities;

public class Utilities {
    public static int MILLISECONDS_IN_A_SECOND = 1000;

    public static String toCurrencyString(final Integer i) {
        if (i == null) {
            return "null";
        }
        final int j = Math.abs(i);
        final int cents = j % 100;
        int start = j / 100;
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('.');
        if (cents < 10) {
            stringBuilder.append('0');
        }
        stringBuilder.append(cents);
        boolean last = true;
        while (true) {
            final int q = start / 1000;
            final int r = start % 1000;
            if (last) {
                last = false;
            } else {
                stringBuilder.insert(0, ',');
            }
            stringBuilder.insert(0, r);
            if (q > 0) {
                if (r < 10) {
                    stringBuilder.insert(0, "00");
                } else if (r < 100) {
                    stringBuilder.insert(0, '0');
                }
                start = q;
            } else {
                break;
            }
        }
        if (i < 0) {
            stringBuilder.insert(0, '-');
        }
        return stringBuilder.toString();
    }
}
