package com.jlim.flashsale_simulation.web;

import com.jlim.flashsale_simulation.dto.*;
import com.jlim.flashsale_simulation.service.PurchaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final JdbcTemplate jdbcTemplate;

    PurchaseController(PurchaseService purchaseService, JdbcTemplate jdbcTemplate){
        this.purchaseService = purchaseService;
        this.jdbcTemplate = jdbcTemplate;
    }

    // MEAT: one user one ticket for now
    @PostMapping("/purchase")
    PurchaseResponse createPurchase (
            @RequestHeader(name = "X-User-Id") String userId,
            @RequestHeader(name = "Idempotency-Key") String idempotencyKey,
            @RequestBody PurchaseRequest req){

        // TODO: delegate, map to response

        // Delegate to the service layer with the generated key
        PurchaseResult result = purchaseService.purchase(req.getEventId(), userId, idempotencyKey);

        // Convert purchase result to purchase response
        return Mapper.fromPurchaseResultToPurchaseResponse(result);
    }

    @GetMapping("/events/{id}")
    EventStatus getEventStatus (@PathVariable long id){
        // read from db using a single join query
        String sql = """
                SELECT
                    e.id AS id,
                    e.capacity AS capacity,
                    i.available AS available,
                    (SELECT COUNT(*) FROM orders o WHERE o.event_id = e.id) AS sold
                FROM events e
                LEFT JOIN inventory i ON e.id = i.event_id
                WHERE e.id = ?
                """;

        return jdbcTemplate.queryForObject(sql, Mapper::mapRowToEventStatus, id);

    }

}
