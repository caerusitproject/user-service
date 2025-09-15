package com.caerus.userservice.dto;

public record UserNotificationDto(
        Long userId,
        String firstName,
        String email,
        String subject,
        String message,
        String countryCode,
        String phoneNumber,
        String whatsappNumber
) {
}
