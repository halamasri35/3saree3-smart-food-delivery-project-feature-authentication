
package com.sar3a.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusLifecycleTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        InMemoryMenuItemAvailability availability =
                new InMemoryMenuItemAvailability();

        availability.addAvailableItem("Burger");

        DeliveryPolicy policy = new DeliveryPolicy(
                new BigDecimal("20.00"), 10.0);

        orderService = new OrderService(
                repository, availability, policy);
    }

    private Order createOrder() {
        return orderService.placeOrder(
                "customer@example.com",
                List.of(new OrderItem(
                        "Burger", 1,
                        new BigDecimal("25.00"))),
                "Nablus, Rafidia Street",
                5.0
        );
    }

    @Test
    void shouldAdvanceThroughAllStatuses() {
        Order order = createOrder();

        OrderStatus[] nextStatuses = {
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING,
                OrderStatus.READY,
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.DELIVERED
        };

        for (OrderStatus next : nextStatuses) {
            orderService.updateOrderStatus(
                    order.getOrderId(), next);

            assertEquals(next, order.getStatus());
        }
    }

    @Test
    void shouldRejectSkippingStatuses() {
        Order order = createOrder();

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(
                        order.getOrderId(),
                        OrderStatus.DELIVERED)
        );

        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void shouldRejectMovingBackwards() {
        Order order = createOrder();

        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.CONFIRMED);

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(
                        order.getOrderId(),
                        OrderStatus.PENDING)
        );

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void shouldRejectCancellationAfterConfirmation() {
        Order order = createOrder();

        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.CONFIRMED);

        assertThrows(
                IllegalStateException.class,
                () -> orderService.cancelOrder(
                        order.getOrderId())
        );

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void shouldRejectCancellationDuringPreparation() {
        Order order = createOrder();

        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.CONFIRMED);

        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.PREPARING);

        assertThrows(
                IllegalStateException.class,
                () -> orderService.cancelOrder(
                        order.getOrderId())
        );

        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }

    @Test
    void shouldRejectChangesAfterDelivery() {
        Order order = createOrder();

        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.CONFIRMED);
        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.PREPARING);
        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.READY);
        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.OUT_FOR_DELIVERY);
        orderService.updateOrderStatus(
                order.getOrderId(),
                OrderStatus.DELIVERED);

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(
                        order.getOrderId(),
                        OrderStatus.CONFIRMED)
        );

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void shouldRejectStatusChangesAfterCancellation() {
        Order order = createOrder();

        orderService.cancelOrder(order.getOrderId());

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(
                        order.getOrderId(),
                        OrderStatus.CONFIRMED)
        );

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldRejectNullStatus() {
        Order order = createOrder();

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(
                        order.getOrderId(), null)
        );
    }
}
