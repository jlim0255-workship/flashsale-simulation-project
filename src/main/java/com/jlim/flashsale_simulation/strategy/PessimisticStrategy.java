package com.jlim.flashsale_simulation.strategy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "purchase.strategy", havingValue = "pessimistic")
public class PessimisticStrategy implements InventoryStrategy {

    private final JdbcTemplate jdbcTemplate;

    public PessimisticStrategy(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean tryDecrement(long eventId) {
        // jdbc update will return the affected row count, not the column value
//        int available = jdbcTemplate.update(
//                "SELECT available FROM inventory WHERE event_id = ? FOR UPDATE", eventId
//        );
        Integer available = jdbcTemplate.queryForObject(
                "SELECT available FROM inventory WHERE event_id = ? FOR UPDATE", Integer.class, eventId

        );

        if (available > 0){
            jdbcTemplate.update(
                    "UPDATE inventory SET available = available - 1 WHERE event_id = ?", eventId
            );
            return true;

        }
        
        return false;
    }
}
