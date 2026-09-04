package com.jlim.flashsale_simulation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SoldOutException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleSoldOut(SoldOutException e) {
        return Map.of("status", "SOLD_OUT");
    }
}
