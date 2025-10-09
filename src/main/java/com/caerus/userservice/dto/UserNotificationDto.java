package com.caerus.userservice.dto;

import java.util.List;

public record UserNotificationDto(
        Long userId,
        String fullName,
        String eventType,
        String email,
        String phoneNumber,
        String whatsappNumber,
        List<String> channels
) {
}
