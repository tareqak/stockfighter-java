package ob.backoffice.abstractions;

import ob.abstractions.Direction;
import ob.abstractions.Order;
import ob.abstractions.OrderType;

import java.time.ZonedDateTime;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OrderStatus {
    private final Accounts.Account account;
    private final Stocks.Stock stock;
    private final Direction direction;
    private final OrderType orderType;
    private final Integer price;
    private final Integer quantity;
    private final ReentrantReadWriteLock reentrantReadWriteLock =
            new ReentrantReadWriteLock();
    private Integer totalFilled = 0;
    private ZonedDateTime lastFilled = null;
    private Integer sharePriceValue = 0;
    private Integer id = null;

    public OrderStatus(final Stocks.Stock stock,
                       final Accounts.Account account,
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

    public OrderStatus(final Order order, final Accounts accounts,
                       final Stocks stocks) {
        direction = order.getDirection();
        orderType = order.getOrderType();
        price = order.getPrice();
        quantity = order.getOriginalQuantity();
        final String venue = order.getVenue();
        account = accounts.getAccount(venue, order.getAccountId());
        stock = stocks.getStock(venue, order.getSymbol());
    }

    public void update(final Integer filled, final Integer sharePriceValue,
                       final ZonedDateTime lastFilled) {
        reentrantReadWriteLock.writeLock().lock();
        totalFilled += filled;
        this.sharePriceValue += sharePriceValue;
        this.lastFilled = lastFilled;
        reentrantReadWriteLock.writeLock().unlock();
    }

    public ZonedDateTime getLastFilled() {
        return lastFilled;
    }

    public Accounts.Account getAccount() {
        return account;
    }

    public Stocks.Stock getStock() {
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
