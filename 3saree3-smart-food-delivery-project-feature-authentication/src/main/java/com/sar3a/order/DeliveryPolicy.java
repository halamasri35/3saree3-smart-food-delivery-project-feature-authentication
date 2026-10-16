
package com.sar3a.order;

import java.math.BigDecimal;
import java.util.Objects;

public class DeliveryPolicy {

    private final BigDecimal minimumOrderValue;
    private final double maximumDeliveryDistanceKm;

    public DeliveryPolicy(
            BigDecimal minimumOrderValue,
            double maximumDeliveryDistanceKm) {

        if (minimumOrderValue == null
                || minimumOrderValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Minimum order value cannot be negative");
        }

        if (!Double.isFinite(maximumDeliveryDistanceKm)
                || maximumDeliveryDistanceKm <= 0) {
            throw new IllegalArgumentException(
                    "Maximum delivery distance must be positive");
        }

        this.minimumOrderValue = minimumOrderValue;
        this.maximumDeliveryDistanceKm = maximumDeliveryDistanceKm;
    }

    public void validate(
            BigDecimal orderTotal,
            String deliveryAddress,
            double deliveryDistanceKm) {

        Objects.requireNonNull(orderTotal, "Order total is required");

        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            throw new IllegalArgumentException(
                    "Delivery address is required");
        }

        if (orderTotal.compareTo(minimumOrderValue) < 0) {
            throw new IllegalArgumentException(
                    "Order is below the minimum order value");
        }

        if (!Double.isFinite(deliveryDistanceKm)
                || deliveryDistanceKm < 0
                || deliveryDistanceKm > maximumDeliveryDistanceKm) {
            throw new IllegalArgumentException(
                    "Restaurant is outside the delivery zone");
        }
    }
}
