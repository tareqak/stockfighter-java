package ob.backoffice.abstractions;

import ob.abstractions.Direction;
import ob.abstractions.OrderType;

import java.time.ZonedDateTime;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OrderStatus {
    private final String account;
    private final Stock stock;
    private final Direction direction;
    private final OrderType orderType;
    private final Integer price;
    private final Integer quantity;
    private Integer totalFilled = 0;
    private ZonedDateTime lastFilled = null;
    private Integer sharePriceValue = 0;
    private Integer id = null;
    private final ReentrantReadWriteLock reentrantReadWriteLock =
            new ReentrantReadWriteLock();

    public OrderStatus(final Stock stock,
                       final String account,
                       final String direction,
                       final String orderType,
                       final Integer price,
                       final Integer quantity) {
        this.stock = stock;
        this.account = account;
        this.direction = Direction.getDirection(direction);
        this.orderType = OrderType.getOrderType(orderType);
        this.price = price;
        this.quantity = quantity;
    }

    public OrderStatus(final Stock stock,
                       final String account,
                       final Direction direction,
                       final OrderType orderType,
                       final Integer price,
                       final Integer quantity) {
        this.stock = stock;
        this.account = account;
        this.direction = direction;
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
    }

    public void update(final Integer filled, final Integer sharePriceValue) {
        reentrantReadWriteLock.writeLock().lock();
        totalFilled += filled;
        this.sharePriceValue += sharePriceValue;
        reentrantReadWriteLock.writeLock().unlock();
    }

    public OrderSnapshot getOrderSnapshot() {
        try {
            reentrantReadWriteLock.readLock().lock();
            return new OrderSnapshot(totalFilled, sharePriceValue);
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }

    public ZonedDateTime getLastFilled() {
        return lastFilled;
    }

    public void setLastFilled(final ZonedDateTime lastFilled) {
        this.lastFilled = lastFilled;
    }

    public String getAccount() {
        return account;
    }

    public Stock getStock() {
        return stock;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Direction getDirection() {
        return direction;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "account=" + account +
                ", stock=" + stock +
                ", direction=" + direction +
                ", orderType=" + orderType +
                ", price=" + price +
                ", quantity=" + quantity +
                ", totalFilled=" + totalFilled +
                ", lastFilled=" + lastFilled +
                ", sharePriceValue=" + sharePriceValue +
                ", id=" + id +
                '}';
    }
}
