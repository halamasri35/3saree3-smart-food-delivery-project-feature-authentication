
package com.sar3a.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private final String orderId;
    private final String customerEmail;
    private final List<OrderItem> items;

    private OrderStatus status;
    private final OrderType orderType;
    private final LocalDateTime scheduledDeliveryTime;

    public Order(
            String orderId,
            String customerEmail,
            List<OrderItem> items) {

        this(orderId, customerEmail, items,
                OrderType.STANDARD, null);
    }

    public Order(
            String orderId,
            String customerEmail,
            List<OrderItem> items,
            OrderType orderType,
            LocalDateTime scheduledDeliveryTime) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException(
                    "Order ID is required");
        }

        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer email is required");
        }

        if (items == null || items.isEmpty()
                || items.stream().anyMatch(item -> item == null)) {
            throw new IllegalArgumentException(
                    "Order must contain valid items");
        }

        if (orderType == null) {
            throw new IllegalArgumentException(
                    "Order type is required");
        }

        if (orderType == OrderType.SCHEDULED) {
            if (scheduledDeliveryTime == null
                    || !scheduledDeliveryTime.isAfter(
                            LocalDateTime.now())) {
                throw new IllegalArgumentException(
                        "Scheduled delivery time must be in the future");
            }
        } else if (scheduledDeliveryTime != null) {
            throw new IllegalArgumentException(
                    "Only scheduled orders can have a delivery time");
        }

        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.items = List.copyOf(items);
        this.status = OrderStatus.PENDING;
        this.orderType = orderType;
        this.scheduledDeliveryTime = scheduledDeliveryTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public LocalDateTime getScheduledDeliveryTime() {
        return scheduledDeliveryTime;
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void advanceStatus(OrderStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "New status is required");
        }

        boolean validTransition = switch (status) {
            case PENDING ->
                    newStatus == OrderStatus.CONFIRMED;

            case CONFIRMED ->
                    newStatus == OrderStatus.PREPARING;

            case PREPARING ->
                    newStatus == OrderStatus.READY;

            case READY ->
                    newStatus == OrderStatus.OUT_FOR_DELIVERY;

            case OUT_FOR_DELIVERY ->
                    newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid order status transition: "
                            + status + " -> " + newStatus);
        }

        this.status = newStatus;
    }

    public void cancel() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending orders can be cancelled");
        }

        status = OrderStatus.CANCELLED;
    }
}
