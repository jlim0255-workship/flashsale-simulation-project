package com.jlim.flashsale_simulation.strategy;

public interface InventoryStrategy {
    /** @return true if a ticket was successfully taken */
    boolean tryDecrement(long eventId);
}
