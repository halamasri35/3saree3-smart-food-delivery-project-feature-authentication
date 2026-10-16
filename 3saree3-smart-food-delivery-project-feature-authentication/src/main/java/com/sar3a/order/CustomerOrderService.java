
package com.sar3a.order;

import java.util.List;
import java.util.Objects;

public class CustomerOrderService {

    private final OrderRepository orderRepository;

    public CustomerOrderService(OrderRepository orderRepository) {
        this.orderRepository =
                Objects.requireNonNull(orderRepository);
    }

    public List<Order> getCustomerOrders(String customerEmail) {

        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer email is required");
        }

        return orderRepository.findByCustomerEmail(customerEmail);
    }

    public Order getCustomerOrderById(
            String customerEmail,
            String orderId) {

        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer email is required");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException(
                    "Order ID is required");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"));

        if (!order.getCustomerEmail()
                .equalsIgnoreCase(customerEmail)) {
            throw new IllegalArgumentException(
                    "Order not found");
        }

        return order;
    }
}
