package com.jlim.flashsale_simulation.strategy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "purchase.strategy", havingValue = "naive")
public class NaiveStrategy implements InventoryStrategy {

    private final JdbcTemplate jdbcTemplate;

    public NaiveStrategy(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean tryDecrement(long eventId){
        /*
         * Check then act race condition (the if available > 0 check)
         * Steps
         * 1) queryForObject the current available
         * 2) an if in Java deciding whether there's stock
         * 3) update that subtracts one
         * */
        Integer available = jdbcTemplate.queryForObject(
                "SELECT available FROM inventory WHERE event_id = ?", Integer.class, eventId
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
