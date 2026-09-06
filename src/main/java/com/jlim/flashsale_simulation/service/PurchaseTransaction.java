package com.jlim.flashsale_simulation.service;

import com.jlim.flashsale_simulation.dto.PurchaseResult;
import com.jlim.flashsale_simulation.exception.SoldOutException;
import com.jlim.flashsale_simulation.strategy.InventoryStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class PurchaseTransaction {

    private final InventoryStrategy strategy;
    private final JdbcTemplate jdbc;

    public PurchaseTransaction(InventoryStrategy strategy, JdbcTemplate jdbc) {
        this.strategy = strategy;
        this.jdbc = jdbc;
    }

    @Transactional
    public PurchaseResult doPurchase(long eventId, String userId, String key) {
        // 1) Insert the claim row with status PENDING. A duplicate idempotency key will raise DuplicateKeyException
        UUID orderId = UUID.randomUUID();
        jdbc.update("INSERT INTO orders (id, event_id, user_id, idempotency_key, status) VALUES (?, ?, ?, ?, ?)", orderId, eventId, userId, key, "PENDING");

        // 2) Try to decrement inventory using the configured strategy
        boolean decremented = strategy.tryDecrement(eventId);
        if (!decremented) {
            // rollback by throwing — the previously inserted PENDING row will be removed when the transaction aborts
            throw new SoldOutException("Tickets Sold Out!!!");
        }

        // 3) Update order to CONFIRMED
        jdbc.update("UPDATE orders SET status = ? WHERE id = ?", "CONFIRMED", orderId);

        // 4) Return result
        return new PurchaseResult(orderId, "CONFIRMED", false);
    }
}