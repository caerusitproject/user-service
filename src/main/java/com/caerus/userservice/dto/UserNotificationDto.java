package com.caerus.userservice.dto;

public record UserNotificationDto(
        Long userId,
        String fullName,
        String eventType,
        String email,
        String countryCode,
        String phoneNumber,
        String whatsappNumber
) {
}
