package com.caerus.userservice.payload;

import lombok.Data;

import java.time.Instant;

@Data
public class ErrorResponse {
    private Instant timestamp;
    private boolean status = false;
    private String error;
    private String message;
    private String path;
    private String correlationId;

    public ErrorResponse(String error, String message, String path, String correlationId) {
        this.timestamp = Instant.now();
        this.error = error;
        this.message = message;
        this.path = path;
        this.correlationId = correlationId;
    }}
