package com.jlim.flashsale_simulation.service;

import com.jlim.flashsale_simulation.dto.PurchaseResult;
import com.jlim.flashsale_simulation.exception.SoldOutException;
import com.jlim.flashsale_simulation.strategy.InventoryStrategy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PurchaseService {

    private final PurchaseTransaction tx;
    private final JdbcTemplate jdbcTemplate;

    public PurchaseService(PurchaseTransaction tx, JdbcTemplate jdbcTemplate) {
        this.tx = tx;
        this.jdbcTemplate = jdbcTemplate;
    }

    public PurchaseResult purchase (long eventId, String userId, String key) {
        /*
        * M3-idempotency
        * INSERT the order with status PENDING and the key
        * strategy.tryDecrement()
        * On success, UPDATE status to CONFIRMED. On failure, throw exception, rollback removes the row and releases the key
        * */

        try {
            return tx.doPurchase(eventId, userId, key);
        }
        catch (DuplicateKeyException e){
            // Fetch the existing order by idempotency key and return it marked as replayed.
            PurchaseResult existing = jdbcTemplate.queryForObject(
                    "SELECT id, status FROM orders WHERE idempotency_key = ?",
                    (rs, rowNum) -> new PurchaseResult(UUID.fromString(rs.getString("id")), rs.getString("status"), true),
                    key
            );

            if (existing == null) {
                throw e; // unexpected — rethrow
            }

            return existing;
        }
    }
}
