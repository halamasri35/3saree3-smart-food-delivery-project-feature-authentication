
package com.sar3a.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryPolicyTest {

    private DeliveryPolicy deliveryPolicy;

    @BeforeEach
    void setUp() {
        deliveryPolicy = new DeliveryPolicy(
                new BigDecimal("20.00"),
                10.0
        );
    }

    @Test
    void shouldAcceptValidDelivery() {
        assertDoesNotThrow(() ->
                deliveryPolicy.validate(
                        new BigDecimal("50.00"),
                        "Nablus, Rafidia Street",
                        5.0
                )
        );
    }

    @Test
    void shouldRejectOrderBelowMinimum() {
        assertThrows(IllegalArgumentException.class, () ->
                deliveryPolicy.validate(
                        new BigDecimal("15.00"),
                        "Nablus, Rafidia Street",
                        5.0
                )
        );
    }

    @Test
    void shouldRejectOutsideDeliveryZone() {
        assertThrows(IllegalArgumentException.class, () ->
                deliveryPolicy.validate(
                        new BigDecimal("50.00"),
                        "Nablus, Rafidia Street",
                        15.0
                )
        );
    }

    @Test
    void shouldRejectEmptyAddress() {
        assertThrows(IllegalArgumentException.class, () ->
                deliveryPolicy.validate(
                        new BigDecimal("50.00"),
                        "",
                        5.0
                )
        );
    }

    @Test
    void shouldAcceptOrderAtMinimumValue() {
        assertDoesNotThrow(() ->
                deliveryPolicy.validate(
                        new BigDecimal("20.00"),
                        "Nablus, Rafidia Street",
                        10.0
                )
        );
    }
}
