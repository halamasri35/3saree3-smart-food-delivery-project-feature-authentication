
package com.sar3a.order;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class InMemoryMenuItemAvailability
        implements MenuItemAvailability {

    private final Set<String> availableItems = new HashSet<>();

    public void addAvailableItem(String itemName) {
        availableItems.add(Objects.requireNonNull(itemName));
    }

    @Override
    public boolean isAvailable(String itemName) {
        return availableItems.contains(itemName);
    }
}
