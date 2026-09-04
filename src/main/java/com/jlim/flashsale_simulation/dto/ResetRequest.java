package com.jlim.flashsale_simulation.dto;

public record ResetRequest(long eventId, int capacity) {
    public long getEventId(){
        return eventId;
    }

    public int getCapacity(){
        return capacity;
    }
}
