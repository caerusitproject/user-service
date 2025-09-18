package com.caerus.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordEvent {
    private Long userId;
    private String email;
    private String resetToken;
    private String eventType;
}