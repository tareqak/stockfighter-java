package ob.backoffice.abstractions;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PositionStatus {
    private final ReentrantReadWriteLock positionReentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private int position = 0;

    public int getPosition() {
        try {
            positionReentrantReadWriteLock.readLock().lock();
            return position;
        } finally {
            positionReentrantReadWriteLock.readLock().unlock();
        }
    }

    public void modifyPosition(Integer newShares) {
        positionReentrantReadWriteLock.writeLock().lock();
        position += newShares;
        positionReentrantReadWriteLock.writeLock().unlock();
    }
}
