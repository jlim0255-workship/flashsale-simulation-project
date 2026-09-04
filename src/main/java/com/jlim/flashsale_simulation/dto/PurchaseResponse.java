package com.jlim.flashsale_simulation.dto;

import java.util.UUID;

public record PurchaseResponse(UUID orderId, String status) {
    public UUID getOrderId() {
        return orderId;
    }

    public String getStatus(){
        return status;
    }


}