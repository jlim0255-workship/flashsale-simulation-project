package com.jlim.flashsale_simulation.dto;

public record InventoryStatus (long eventId, int available, int version) {
    public long getEventId(){
        return eventId;
    }

    public int getAvailable() {
        return available;
    }

    public int getVersion() {
        return version;
    }
}

