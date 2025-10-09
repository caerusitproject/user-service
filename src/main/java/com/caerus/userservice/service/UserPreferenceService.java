package com.caerus.userservice.service;

import com.caerus.userservice.domain.User;
import com.caerus.userservice.domain.UserPreference;
import com.caerus.userservice.dto.UserPreferenceDto;

public interface UserPreferenceService {
    void createDefaultPreferences(User user);
    void updatePreferences(Long userId, UserPreferenceDto preferenceDto);
    UserPreference getPreferences(Long userId);
}