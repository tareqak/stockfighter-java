package ob.backoffice.abstractions;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CashStatus {
    private final ReentrantReadWriteLock cashReentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private int cash;

    public CashStatus(final int cash) {
        this.cash = cash;
    }

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
