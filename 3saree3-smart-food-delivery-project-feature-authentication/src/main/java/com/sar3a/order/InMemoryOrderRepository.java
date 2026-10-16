
package com.sar3a.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new HashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getOrderId(), order);
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public List<Order> findByCustomerEmail(String customerEmail) {
        return orders.values().stream()
                .filter(order ->
                        order.getCustomerEmail()
                                .equalsIgnoreCase(customerEmail))
                .toList();
    }
}
