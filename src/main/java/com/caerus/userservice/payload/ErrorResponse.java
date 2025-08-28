package com.caerus.userservice.payload;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {
    private boolean status = false;
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
