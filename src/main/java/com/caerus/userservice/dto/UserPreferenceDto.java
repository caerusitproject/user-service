package com.caerus.userservice.dto;

public record UserPreferenceDto(
        boolean emailEnabled,
        boolean smsEnabled,
        boolean whatsappEnabled
) {}
