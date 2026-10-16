
package com.sar3a.order;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {

    private final String itemName;
    private final int quantity;
    private final BigDecimal unitPrice;

    
public OrderItem(String itemName, int quantity, BigDecimal unitPrice) {

    if (itemName == null || itemName.isBlank()) {
        throw new IllegalArgumentException("Item name cannot be empty");
    }

    if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Item price cannot be negative");
    }

    this.itemName = itemName;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
}


    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
