package com.jlim.flashsale_simulation.dto;

import java.util.UUID;

public record PurchaseResult(UUID orderId, String status, boolean replayed) {

    public UUID getOrderId(){
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isReplayed() {
        return replayed;
    }
}
