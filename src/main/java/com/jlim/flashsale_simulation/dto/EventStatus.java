package com.jlim.flashsale_simulation.dto;

public record EventStatus(long eventId, int capacity, int available, int sold) {
    public long getEventId(){
        return eventId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailable() {
        return available;
    }

    public int getSold() {
        return sold;
    }
}