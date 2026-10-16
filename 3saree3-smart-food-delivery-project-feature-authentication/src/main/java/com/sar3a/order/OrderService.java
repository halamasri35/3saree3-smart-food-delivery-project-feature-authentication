
package com.sar3a.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemAvailability menuItemAvailability;
    private final DeliveryPolicy deliveryPolicy;

    public OrderService(
            OrderRepository orderRepository,
            MenuItemAvailability menuItemAvailability,
            DeliveryPolicy deliveryPolicy) {

        this.orderRepository =
                Objects.requireNonNull(orderRepository);

        this.menuItemAvailability =
                Objects.requireNonNull(menuItemAvailability);

        this.deliveryPolicy =
                Objects.requireNonNull(deliveryPolicy);
    }

    public Order placeOrder(
            String customerEmail,
            List<OrderItem> items,
            String deliveryAddress,
            double deliveryDistanceKm) {

        return placeOrder(
                customerEmail,
                items,
                deliveryAddress,
                deliveryDistanceKm,
                OrderType.STANDARD,
                null
        );
    }

    public Order placeOrder(
            String customerEmail,
            List<OrderItem> items,
            String deliveryAddress,
            double deliveryDistanceKm,
            OrderType orderType,
            LocalDateTime scheduledDeliveryTime) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one item");
        }

        for (OrderItem item : items) {
            if (item == null) {
                throw new IllegalArgumentException(
                        "Order cannot contain null items");
            }

            if (!menuItemAvailability.isAvailable(
                    item.getItemName())) {
                throw new IllegalArgumentException(
                        "Item is not available: "
                                + item.getItemName());
            }
        }

        Order order = new Order(
                UUID.randomUUID().toString(),
                customerEmail,
                items,
                orderType,
                scheduledDeliveryTime
        );

        deliveryPolicy.validate(
                order.calculateTotal(),
                deliveryAddress,
                deliveryDistanceKm
        );

        orderRepository.save(order);

        return order;
    }

    public Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"));
    }

    public Order cancelOrder(String orderId) {
        Order order = findOrderById(orderId);

        order.cancel();
        orderRepository.save(order);

        return order;
    }

    public Order updateOrderStatus(
            String orderId,
            OrderStatus newStatus) {

        Order order = findOrderById(orderId);

        order.advanceStatus(newStatus);
        orderRepository.save(order);

        return order;
    }
}
