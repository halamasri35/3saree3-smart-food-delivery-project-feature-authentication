
package com.sar3a.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();

        InMemoryMenuItemAvailability availability =
                new InMemoryMenuItemAvailability();

        availability.addAvailableItem("Burger");
        availability.addAvailableItem("Fries");
        availability.addAvailableItem("Juice");
        availability.addAvailableItem("Pizza");

        DeliveryPolicy policy =
                new DeliveryPolicy(
                        new BigDecimal("20.00"),
                        10.0
                );

        orderService = new OrderService(
                repository,
                availability,
                policy
        );
    }

    private Order placeValidOrder(List<OrderItem> items) {
        return orderService.placeOrder(
                "customer@example.com",
                items,
                "Nablus, Rafidia Street",
                5.0
        );
    }

    @Test
    void shouldCreateOrderWithPendingStatus() {
        Order order = placeValidOrder(List.of(
                new OrderItem(
                        "Burger", 2, new BigDecimal("25.00"))
        ));

        assertNotNull(order.getOrderId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void shouldCalculateCorrectOrderTotal() {
        Order order = placeValidOrder(List.of(
                new OrderItem(
                        "Burger", 2, new BigDecimal("25.00")),
                new OrderItem(
                        "Fries", 1, new BigDecimal("10.00")),
                new OrderItem(
                        "Juice", 2, new BigDecimal("8.00"))
        ));

        assertEquals(
                0,
                new BigDecimal("76.00")
                        .compareTo(order.calculateTotal())
        );
    }

    @Test
    void shouldSaveAndRetrieveOrder() {
        Order created = placeValidOrder(List.of(
                new OrderItem(
                        "Pizza", 1, new BigDecimal("35.00"))
        ));

        Order retrieved =
                orderService.findOrderById(created.getOrderId());

        assertEquals(
                created.getOrderId(),
                retrieved.getOrderId()
        );
    }

    @Test
    void shouldRejectUnknownOrderId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.findOrderById("unknown-id")
        );
    }

    @Test
    void shouldRejectEmptyOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> placeValidOrder(List.of())
        );
    }

    @Test
    void shouldRejectZeroQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem(
                        "Burger", 0, new BigDecimal("25.00"))
        );
    }

    @Test
    void shouldRejectNegativeQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem(
                        "Burger", -2, new BigDecimal("25.00"))
        );
    }

    @Test
    void shouldRejectNegativePrice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem(
                        "Burger", 1, new BigDecimal("-5.00"))
        );
    }

    @Test
    void shouldRejectMissingCustomerEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(
                        "",
                        List.of(new OrderItem(
                                "Burger", 1,
                                new BigDecimal("25.00"))),
                        "Nablus, Rafidia Street",
                        5.0
                )
        );
    }

    @Test
    void shouldRejectUnavailableItem() {
        assertThrows(
                IllegalArgumentException.class,
                () -> placeValidOrder(List.of(
                        new OrderItem(
                                "Sushi", 1,
                                new BigDecimal("40.00"))
                ))
        );
    }

    @Test
    void shouldAcceptAvailableItem() {
        Order order = placeValidOrder(List.of(
                new OrderItem(
                        "Burger", 1,
                        new BigDecimal("25.00"))
        ));

        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void shouldRejectOrderBelowMinimumValue() {
        assertThrows(
                IllegalArgumentException.class,
                () -> placeValidOrder(List.of(
                        new OrderItem(
                                "Fries", 1,
                                new BigDecimal("10.00"))
                ))
        );
    }

    @Test
    void shouldRejectOrderOutsideDeliveryZone() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(
                        "customer@example.com",
                        List.of(new OrderItem(
                                "Burger", 1,
                                new BigDecimal("25.00"))),
                        "Nablus, Rafidia Street",
                        15.0
                )
        );
    }

    @Test
    void shouldRejectInvalidDeliveryAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(
                        "customer@example.com",
                        List.of(new OrderItem(
                                "Burger", 1,
                                new BigDecimal("25.00"))),
                        "",
                        5.0
                )
        );
    }

    @Test
    void shouldRejectInvalidOrderBeforeSaving() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(
                        "customer@example.com",
                        List.of(new OrderItem(
                                "Burger", 1,
                                new BigDecimal("25.00"))),
                        "Nablus, Rafidia Street",
                        15.0
                )
        );
    }

    @Test
    void shouldCancelPendingOrder() {
        Order order = placeValidOrder(List.of(
                new OrderItem(
                        "Burger", 1,
                        new BigDecimal("25.00"))
        ));

        Order cancelled =
                orderService.cancelOrder(order.getOrderId());

        assertEquals(
                OrderStatus.CANCELLED,
                cancelled.getStatus()
        );

        assertEquals(
                OrderStatus.CANCELLED,
                orderService.findOrderById(
                        order.getOrderId()).getStatus()
        );
    }

    @Test
    void shouldRejectCancellingAlreadyCancelledOrder() {
        Order order = placeValidOrder(List.of(
                new OrderItem(
                        "Burger", 1,
                        new BigDecimal("25.00"))
        ));

        orderService.cancelOrder(order.getOrderId());

        assertThrows(
                IllegalStateException.class,
                () -> orderService.cancelOrder(order.getOrderId())
        );
    }

    @Test
    void shouldRejectCancellingUnknownOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.cancelOrder("unknown-id")
        );
    }

    @Test
    void shouldCreateStandardOrderByDefault() {
        Order order = placeValidOrder(List.of(
                new OrderItem(
                        "Burger", 1,
                        new BigDecimal("25.00"))
        ));

        assertEquals(
                OrderType.STANDARD,
                order.getOrderType()
        );

        assertNull(order.getScheduledDeliveryTime());
    }

    @Test
    void shouldCreateExpressOrder() {
        Order order = orderService.placeOrder(
                "customer@example.com",
                List.of(new OrderItem(
                        "Burger", 1,
                        new BigDecimal("25.00"))),
                "Nablus, Rafidia Street",
                5.0,
                OrderType.EXPRESS,
                null
        );

        assertEquals(OrderType.EXPRESS, order.getOrderType());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void shouldCreateScheduledOrderWithFutureTime() {
        LocalDateTime future =
                LocalDateTime.now().plusDays(1);

        Order order = orderService.placeOrder(
                "customer@example.com",
                List.of(new OrderItem(
                        "Pizza", 1,
                        new BigDecimal("35.00"))),
                "Nablus, Rafidia Street",
                5.0,
                OrderType.SCHEDULED,
                future
        );

        assertEquals(
                OrderType.SCHEDULED,
                order.getOrderType()
        );

        assertEquals(
                future,
                order.getScheduledDeliveryTime()
        );
    }

    @Test
    void shouldRejectScheduledOrderWithPastTime() {
        LocalDateTime past =
                LocalDateTime.now().minusDays(1);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(
                        "customer@example.com",
                        List.of(new OrderItem(
                                "Pizza", 1,
                                new BigDecimal("35.00"))),
                        "Nablus, Rafidia Street",
                        5.0,
                        OrderType.SCHEDULED,
                        past
                )
        );
    }

    @Test
    void shouldRejectScheduledOrderWithoutTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(
                        "customer@example.com",
                        List.of(new OrderItem(
                                "Pizza", 1,
                                new BigDecimal("35.00"))),
                        "Nablus, Rafidia Street",
                        5.0,
                        OrderType.SCHEDULED,
                        null
                )
        );
    }

    @Test
    void shouldRejectDeliveryTimeForExpressOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(
                        "customer@example.com",
                        List.of(new OrderItem(
                                "Burger", 1,
                                new BigDecimal("25.00"))),
                        "Nablus, Rafidia Street",
                        5.0,
                        OrderType.EXPRESS,
                        LocalDateTime.now().plusHours(2)
                )
        );
    }
}
