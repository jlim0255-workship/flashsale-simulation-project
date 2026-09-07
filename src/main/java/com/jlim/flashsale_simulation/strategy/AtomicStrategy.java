package com.jlim.flashsale_simulation.strategy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "purchase.strategy", havingValue = "atomic")
public class AtomicStrategy implements InventoryStrategy{

    private final JdbcTemplate jdbcTemplate;

    public AtomicStrategy(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean tryDecrement(long eventId) {
        int rows = jdbcTemplate.update(
                "UPDATE inventory SET available = available - 1 WHERE event_id = ? AND available > 0", eventId
        );

        return rows == 1;
    }
}
