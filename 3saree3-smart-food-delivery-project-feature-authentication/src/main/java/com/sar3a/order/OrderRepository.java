
package com.sar3a.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(String orderId);

    List<Order> findByCustomerEmail(String customerEmail);
}
