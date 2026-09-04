package com.jlim.flashsale_simulation.dto;

public record PurchaseRequest(long eventId) {
    public long getEventId(){
        return eventId;
    }
}