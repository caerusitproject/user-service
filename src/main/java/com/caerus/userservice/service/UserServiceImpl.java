package com.caerus.userservice.service;

import com.caerus.userservice.configure.ModelMapperConfig;
import com.caerus.userservice.dto.*;
import com.caerus.userservice.enums.RoleType;
import com.caerus.userservice.enums.UserEventType;
import com.caerus.userservice.exception.ResourceAlreadyExistsException;
import com.caerus.userservice.mapper.RegisterMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.caerus.userservice.exception.NotFoundException;
import com.caerus.userservice.domain.Role;
import com.caerus.userservice.domain.User;
import com.caerus.userservice.repository.RoleRepository;
import com.caerus.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
    private final ModelMapperConfig modelMapper;
    private final RegisterMapper registerMapper;
    private final ProducerTemplate producerTemplate;
    private final PasswordResetTokenService passwordResetTokenService;

    @Override
    public Long saveUser(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(user -> {
                    throw new ResourceAlreadyExistsException(
                            "User with email " + registerRequest.getEmail() + " already exists"
                    );
                });

        if (registerRequest.getRole() == null || registerRequest.getRole().isEmpty()) {
            Role defaultRole = roleRepository.findByName(RoleType.USER_ROLE.name())
                    .orElseGet(this::createDefaultUserRole);
            registerRequest.setRole(Collections.singleton(defaultRole.getName()));
        }

        User user = registerMapper.toEntity(registerRequest);

        user.setUsername(registerRequest.getEmail());
        user.setIsActive(false);

        if (registerRequest.getPassword() != null && !registerRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        }

        if (registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()) {
            Set<Role> roles = registerRequest.getRole().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);

        //publish event
        UserNotificationDto event = new UserNotificationDto(savedUser.getId(), savedUser.getFirstName() +" "+ savedUser.getLastName(),
                UserEventType.USER_REGISTERED.name(), savedUser.getEmail(), savedUser.getCountryCode(), savedUser.getPhoneNumber(), savedUser.getPhoneNumber());

        producerTemplate.sendBody("direct:user-events", event);
        log.info("User registered event published: {}", event);

        return savedUser.getId();
    }


    private Role createDefaultUserRole() {
        Role role = new Role();
        role.setName("USER_ROLE");
        return roleRepository.save(role);
    }

    private User mapDtoToUser(RegisterRequest registerRequest, User existingUser) {
        User user = (existingUser != null) ? existingUser : new User();

        modelMapper.getModelMapper().map(registerRequest, user);

        if (existingUser == null) {
            user.setUsername(registerRequest.getEmail());
        }

        if (registerRequest.getPassword() != null && !registerRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        }

        if (registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()) {
            Set<Role> roles = registerRequest.getRole().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

            return user;
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
        return users.map(this::mapToDto);
    }

    private RegisterRequest mapToDto(User user) {
        RegisterRequest dto = modelMapper.getModelMapper().map(user, RegisterRequest.class);

        dto.setRole(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );

        return dto;
    }

    @Override
    public RegisterRequest findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + userId));

        return mapToDto(user);
    }

    @Override
    public RegisterRequest findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email " + email));

        return mapToDto(user);
    }

    @Override
    public RegisterRequest findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username " + username));

        return mapToDto(user);
    }

    public void deleteUserById(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        user.getRoles().clear();
        userRepository.delete(user);
	}

    @Override
    public RegisterRequest updateUserById(Long userId, UserUpdateDto userUpdateDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        RegisterRequest registerRequest = modelMapper.getModelMapper().map(userUpdateDto, RegisterRequest.class);

        if (!existingUser.getEmail().equals(registerRequest.getEmail())) {
            userRepository.findByEmail(registerRequest.getEmail()).ifPresent(user -> {
                throw new ResourceAlreadyExistsException("User with email " + registerRequest.getEmail() + " already exists");
            });
        }

        User updatedUser = mapDtoToUser(registerRequest, existingUser);
        updatedUser = userRepository.save(updatedUser);

        return mapToDto(updatedUser);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email " + email));

        String resetToken = UUID.randomUUID().toString();

        passwordResetTokenService.saveToken(user.getId(), resetToken, Duration.ofMinutes(15));

        ForgotPasswordEvent event = new ForgotPasswordEvent(user.getId(), user.getEmail(),resetToken,
                UserEventType.PASSWORD_RESET_REQUESTED.name());

        producerTemplate.sendBody("direct:forgot-password-events", event);
        log.info("Forgot password event published for user: {}", user.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Long userId = passwordResetTokenService.validateToken(request.token()); //!!!Attention change this to email

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        passwordResetTokenService.invalidateToken(request.token());

        log.info("Password successfully reset for user: {}", user.getEmail());
    }

}