package com.jlim.flashsale_simulation.dto;

//import java.util.UUID;

import java.sql.ResultSet;
import java.sql.SQLException;

//public record PurchaseResponse(UUID orderId, String status) {}
public class Mapper {
    public static PurchaseResponse fromPurchaseResultToPurchaseResponse(PurchaseResult purchaseResult){
        return new PurchaseResponse(
                purchaseResult.getOrderId(),
                purchaseResult.getStatus()
        );
    }

    public static EventStatus mapRowToEventStatus(ResultSet rs, int rowNum) throws SQLException {
        return new EventStatus(
                rs.getLong("id"),
                rs.getInt("capacity"),
                rs.getInt("available"),
                rs.getInt("sold")
        );
    }
}
