package com.jlim.flashsale_simulation.service;

import com.jlim.flashsale_simulation.dto.PurchaseResult;
import com.jlim.flashsale_simulation.exception.SoldOutException;
import com.jlim.flashsale_simulation.strategy.InventoryStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PurchaseService {

    private final InventoryStrategy strategy;
    private final JdbcTemplate jdbcTemplate;

    public PurchaseService(InventoryStrategy strategy, JdbcTemplate jdbcTemplate) {
        this.strategy = strategy;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public PurchaseResult purchase (long eventId, String userId) {
        /*
        * if (!strategy.tryDecrement(eventId)) throw new SoldOutException();
        * UUID orderId = UUID.randomUUID();
        * jdbcTemplate.update("INSERT INTO orders (id, event_id, user_id, idempotency_key, status) VALUES (?, ?, ?, ?, ?)", orderId, eventId, userId, null, "CONFIRMED");
        * return new PurchaseResult(orderId, "CONFIRMED");
        * */

        // throw exception to rollback if tryDecrement return false
        if (!strategy.tryDecrement(eventId)){
            throw new SoldOutException("Tickets Sold Out!!!");
        }

        UUID orderId = UUID.randomUUID();

        jdbcTemplate.update("INSERT INTO orders (id, event_id, user_id, idempotency_key, status) VALUES (?, ?, ?, ?, ?)", orderId, eventId, userId, null, "CONFIRMED");

        return new PurchaseResult(orderId, "CONFIRMED");
    }
}
