package com.caerus.userservice.service;

import com.caerus.userservice.domain.User;
import com.caerus.userservice.domain.UserPreference;
import com.caerus.userservice.dto.UserPreferenceDto;
import com.caerus.userservice.repository.UserPreferenceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserPreferenceRepository preferenceRepository;

    @Override
    public void createDefaultPreferences(User user) {
        UserPreference preference = UserPreference.builder()
                .user(user)
                .emailEnabled(true)
                .smsEnabled(true)
                .whatsappEnabled(true)
                .build();

        preferenceRepository.save(preference);
        log.info("Default notification preferences created for user {}", user.getId());
    }

    @Override
    public void updatePreferences(Long userId, UserPreferenceDto dto) {
        UserPreference preference = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User preference not found for ID: " + userId));

        preference.setEmailEnabled(dto.emailEnabled());
        preference.setSmsEnabled(dto.smsEnabled());
        preference.setWhatsappEnabled(dto.whatsappEnabled());

        preferenceRepository.save(preference);
        log.info("Updated preferences for user {}", userId);
    }

    @Override
    public UserPreference getPreferences(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User preference not found for ID: " + userId));
    }
}
