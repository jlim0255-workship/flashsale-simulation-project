package com.jlim.flashsale_simulation.web;

import com.jlim.flashsale_simulation.dto.EventStatus;
import com.jlim.flashsale_simulation.dto.ResetRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    // dependency injection
    private final JdbcTemplate jdbcTemplate;

    public AdminController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/reset")
    @Transactional
    public EventStatus resetEvent(@RequestBody ResetRequest req){
        long eventId = req.getEventId();
        int newCapacity = req.getCapacity();

        // 1 clear existing order
        jdbcTemplate.update("DELETE FROM orders WHERE event_id = ?", eventId);


        // 2 inventory available tickets
        jdbcTemplate.update("UPDATE inventory SET available = ?, version = 0 WHERE event_id = ?", newCapacity, eventId);

        // 3 update event capacity
        jdbcTemplate.update("UPDATE events SET capacity = ? WHERE id = ?", newCapacity, eventId);

        // Return fresh EventStatus so one curl resets and confirms
        return new EventStatus(eventId, newCapacity, newCapacity, 0);
    }

}
