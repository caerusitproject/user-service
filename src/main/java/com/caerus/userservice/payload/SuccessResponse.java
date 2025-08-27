package com.caerus.userservice.payload;

import lombok.Data;

@Data
public class SuccessResponse<T> {
    private boolean status = true;
    private String message = "Data retrieved successfully";
    private T data;

    public SuccessResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public SuccessResponse(T data) {
        this.data = data;
    }
}
