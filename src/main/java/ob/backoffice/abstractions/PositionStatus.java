package ob.backoffice.abstractions;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PositionStatus {
    private final ReentrantReadWriteLock positionReentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private int position = 0;
    private int sharePriceValue = 0;

    public PositionSnapshot getPositionSnapshot() {
        try {
            positionReentrantReadWriteLock.readLock().lock();
            return new PositionSnapshot(position, sharePriceValue);
        } finally {
            positionReentrantReadWriteLock.readLock().unlock();
        }
    }

    public void modifyPosition(final Integer shares,
                               final Integer incomingSharePriceValue) {
        positionReentrantReadWriteLock.writeLock().lock();
        if (position >= 0) {
            if (shares > 0) {
                // BUY
                sharePriceValue += incomingSharePriceValue;
                position += shares;
            } else if (Math.abs(shares) <= position) {
                // SELL but position will stay 0 or more
                if (position > 0) {
                    sharePriceValue += sharePriceValue / position * shares;
                    position += shares;
                }
            } else {
                // SELL into a short position
                int averageSharePrice = incomingSharePriceValue / shares;
                position += shares;
                sharePriceValue = averageSharePrice * position;
            }
        } else {
            if (shares < 0) {
                // SELL
                sharePriceValue += incomingSharePriceValue;
                position += shares;
            } else if (Math.abs(position) >= shares) {
                // BUY but but position will stay 0 or less
                sharePriceValue += sharePriceValue / position * shares;
                position += shares;
            } else {
                // BUY into a long position
                if (shares > 0) {
                    int averageSharePrice = incomingSharePriceValue / shares;
                    position += shares;
                    sharePriceValue = Math.abs(averageSharePrice * position);
                }
            }
        }
        positionReentrantReadWriteLock.writeLock().unlock();
    }
}
