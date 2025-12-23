package com.caerus.userservice.service;

import com.caerus.userservice.domain.UserPreference;
import com.caerus.userservice.dto.*;
import com.caerus.userservice.enums.Channel;
import com.caerus.userservice.enums.RoleType;
import com.caerus.userservice.enums.UserEventType;
import com.caerus.userservice.exception.ResourceAlreadyExistsException;
import com.caerus.userservice.mapper.UserMapper;
import com.caerus.userservice.messaging.UserEventPublisher;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.caerus.userservice.exception.NotFoundException;
import com.caerus.userservice.domain.Role;
import com.caerus.userservice.domain.User;
import com.caerus.userservice.repository.RoleRepository;
import com.caerus.userservice.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final UserPreferenceService userPreferenceService;
    private final UserEventPublisher userEventPublisher;

    @Override
    public Long saveUser(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(user -> {
                    throw new ResourceAlreadyExistsException(
                            "User with email " + registerRequest.getEmail() + " already exists"
                    );
                });

        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName(RoleType.USER.name())
                    .orElseGet(this::createDefaultUserRole);
            registerRequest.setRoles(Collections.singleton(defaultRole.getName()));
        }

        User user = userMapper.toEntity(registerRequest);

        user.setUsername(registerRequest.getEmail());
        user.setIsActive(false);

        if (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()) {
            Set<Role> roles = registerRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);

        userPreferenceService.createDefaultPreferences(savedUser);

        var preferences = userPreferenceService.getPreferences(savedUser.getId());
        List<String> channels = buildEnabledChannels(preferences);

        String fullPhone = formatPhoneNumber(savedUser.getCountryCode(), savedUser.getPhoneNumber());

        //publish event
        UserNotificationDto event = new UserNotificationDto(savedUser.getId(), savedUser.getFirstName() +" "+ savedUser.getLastName(),
                UserEventType.USER_REGISTERED.name(), savedUser.getEmail(), fullPhone, fullPhone, channels);

        userEventPublisher.publish(event);
        log.info("User registered event published: {}", event);

        return savedUser.getId();
    }

    private String formatPhoneNumber(String countryCode, String phoneNumber) {
        String normalized = phoneNumber.replaceAll("[^0-9]","");
        return countryCode.startsWith("+") ? countryCode + normalized : "+" + countryCode + normalized;

    }

    private List<String> buildEnabledChannels(UserPreference preferences) {
        List<String> channels = new ArrayList<>();
        if (Boolean.TRUE.equals(preferences.getEmailEnabled())) channels.add(Channel.EMAIL.name());
        if (Boolean.TRUE.equals(preferences.getSmsEnabled())) channels.add(Channel.SMS.name());
        if (Boolean.TRUE.equals(preferences.getWhatsappEnabled())) channels.add(Channel.WHATSAPP.name());
        return channels;
    }

    private Role createDefaultUserRole() {
        Role role = new Role();
        role.setName("USER");
        return roleRepository.save(role);
    }

    @Override
    public Page<RegisterRequest> getAllUsers(String search, Pageable pageable) {
        Page<User> users;

        if(search!=null && !search.isBlank()){
            users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, search, pageable);
        } else{
            users = userRepository.findAll(pageable);
        }
        return users.map(userMapper::toDto);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    @Override
    public RegisterRequest findUserById(Long userId) {
        User user = getUserOrThrow(userId);

        return userMapper.toDto(user);
    }

    @Override
    public RegisterRequest findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email " + email));

        return userMapper.toDto(user);
    }

    @Override
    public RegisterRequest findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username " + username));

        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteUserById(Long userId) {
		User user = getUserOrThrow(userId);

        user.getRoles().clear();
        userRepository.delete(user);
	}

    @Override
    @Transactional
    public RegisterRequest updateUserById(Long userId, UserUpdateDto userUpdateDto) {
        User existingUser = getUserOrThrow(userId);

        if (userUpdateDto.getEmail() != null &&
                !existingUser.getEmail().equals(userUpdateDto.getEmail())) {

            userRepository.findByEmail(userUpdateDto.getEmail()).ifPresent(user -> {
                throw new ResourceAlreadyExistsException("User with email " + userUpdateDto.getEmail() + " already exists");
            });
        }

        userMapper.updateUserFromDto(userUpdateDto, existingUser);

        if(userUpdateDto.getRoles() != null){
            Set<Role> roles = userUpdateDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);

    }

    @Override
    public UserRolesDto getUserByEmailForInternalService(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email " + email));

        return userMapper.toUserRolesDto(user);
    }
}