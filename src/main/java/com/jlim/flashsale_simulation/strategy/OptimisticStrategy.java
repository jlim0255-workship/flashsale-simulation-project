package com.jlim.flashsale_simulation.strategy;

import com.jlim.flashsale_simulation.dto.InventoryStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "purchase.strategy", havingValue = "optimistic")
public class OptimisticStrategy implements InventoryStrategy{

    private final JdbcTemplate jdbcTemplate;
    private static int MAX_RETRIES = 5;

    public OptimisticStrategy (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean tryDecrement(long eventId) {
        // TODO: wrap the fields from DB into a type to access
        for (int attempt = 0; attempt < MAX_RETRIES ; attempt++){
            // 1. SELECT available, version (no lock)
            InventoryStatus status = jdbcTemplate.queryForObject(
                    "SELECT available, version FROM inventory WHERE event_id = ?",
                    (rs, rowNum) -> {
                        InventoryStatus selected = new InventoryStatus(
                                eventId,
                                rs.getInt("available"),
                                rs.getInt("version")
                        );
                        return selected;
                    },
                    eventId
            );
            // 2. if available <= 0 -> return false
            if (status.getAvailable() <= 0){
                return false;
            }

            // 3. Increment the version AND check rows affected
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE inventory SET available = available - 1, version = version + 1 " +
                            "WHERE event_id = ? AND version = ?",
                    eventId,
                    status.getVersion()
            );

            // 4. Success check: Immediately return true if we won the race
            if (rowsAffected == 1) {
                return true;
            }
        }

        // 5. return false after max retries
        return false;

    }
}
