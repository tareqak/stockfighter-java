package ob.backoffice.abstractions;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CashStatus {
    private final ReentrantReadWriteLock cashReentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private int cash = 0;

    public int getCash() {
        try {
            cashReentrantReadWriteLock.readLock().lock();
            return cash;
        } finally {
            cashReentrantReadWriteLock.readLock().unlock();
        }
    }

    public void modifyCash(final int newCash) {
        cashReentrantReadWriteLock.writeLock().lock();
        cash += newCash;
        cashReentrantReadWriteLock.writeLock().unlock();
    }
}
