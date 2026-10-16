
package com.sar3a.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerOrderServiceTest {

    private CustomerOrderService customerOrderService;
    private InMemoryOrderRepository repository;

    private Order customerOneOrder;
    private Order customerTwoOrder;

    @BeforeEach
    void setUp() {

        repository = new InMemoryOrderRepository();

        customerOrderService =
                new CustomerOrderService(repository);

        customerOneOrder = new Order(
                "order-101",
                "customer1@example.com",
                List.of(new OrderItem(
                        "Burger",
                        1,
                        new BigDecimal("25.00")
                ))
        );

        customerTwoOrder = new Order(
                "order-202",
                "customer2@example.com",
                List.of(new OrderItem(
                        "Pizza",
                        1,
                        new BigDecimal("35.00")
                ))
        );

        repository.save(customerOneOrder);
        repository.save(customerTwoOrder);
    }

    @Test
    void shouldReturnOnlyCustomerOrders() {

        List<Order> result =
                customerOrderService.getCustomerOrders(
                        "customer1@example.com"
                );

        assertEquals(1, result.size());
        assertEquals("order-101", result.get(0).getOrderId());
    }

    @Test
    void shouldReturnCustomerOrderById() {

        Order result =
                customerOrderService.getCustomerOrderById(
                        "customer1@example.com",
                        "order-101"
                );

        assertEquals(
                customerOneOrder.getOrderId(),
                result.getOrderId()
        );
    }

    @Test
    void shouldRejectAccessToAnotherCustomerOrder() {

        assertThrows(
                IllegalArgumentException.class,
                () -> customerOrderService.getCustomerOrderById(
                        "customer1@example.com",
                        "order-202"
                )
        );
    }

    @Test
    void shouldRejectUnknownOrder() {

        assertThrows(
                IllegalArgumentException.class,
                () -> customerOrderService.getCustomerOrderById(
                        "customer1@example.com",
                        "unknown-order"
                )
        );
    }

    @Test
    void shouldRejectMissingCustomerEmail() {

        assertThrows(
                IllegalArgumentException.class,
                () -> customerOrderService.getCustomerOrders("")
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersMatch() {

        List<Order> result =
                customerOrderService.getCustomerOrders(
                        "unknown@example.com"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnMultipleOrdersForSameCustomer() {

        repository.save(new Order(
                "order-303",
                "customer1@example.com",
                List.of(new OrderItem(
                        "Fries",
                        2,
                        new BigDecimal("10.00")
                ))
        ));

        List<Order> result =
                customerOrderService.getCustomerOrders(
                        "customer1@example.com"
                );

        assertEquals(2, result.size());
    }
}
