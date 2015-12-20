package ob.backoffice.abstractions;

public class OrderStatusContainer {
    private OrderStatus orderStatus = null;

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "OrderStatusContainer{" + orderStatus + '}';
    }
}
