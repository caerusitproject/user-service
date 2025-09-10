package com.caerus.userservice.service;

import com.caerus.userservice.enums.RoleType;
import com.caerus.userservice.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.apache.camel.ProducerTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.caerus.userservice.configure.ModelMapperConfig;
import com.caerus.userservice.dto.UserDto;
import com.caerus.userservice.dto.UserNotification;
import com.caerus.userservice.exception.NotFoundException;
import com.caerus.userservice.model.Role;
import com.caerus.userservice.model.User;
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
    private final ProducerTemplate producerTemplate;

	@Override
	public Long saveUser(UserDto userDto) {

        userRepository.findByEmail(userDto.getEmail()).ifPresent(user -> {
            throw new ResourceAlreadyExistsException(
                    "User with email " + userDto.getEmail() + " already exists"
            );
        });

        if (userDto.getRole() == null) {
            Role defaultRole = roleRepository.findByName(RoleType.USER_ROLE.name())
                    .orElseGet(this::createDefaultUserRole);
            userDto.setRole(Collections.singleton(defaultRole.getName()));
        }

        User user = mapDtoToUser(userDto, null);
        user.setIsActive(false);
        User savedUser = userRepository.save(user);
        
        //process through camel route to publish to kafka
        //producerTemplate.requestBody("direct:userSaveAndPublish", user, User.class);
        
        UserNotification userNotification = new UserNotification(
				savedUser.getId()!=null ? savedUser.getId():0L,
				savedUser.getEmail(),
				"User creation successful",
				"Welcome " + savedUser.getFirstName() + " " + savedUser.getLastName() + "!"
						+ " Your account has been created successfully. Your username is: " + savedUser.getUsername(),
				savedUser.getPhone(),
				savedUser.getPhone()
		);
        
        producerTemplate.sendBody("direct:userSaveAndNotify", userNotification);

        return savedUser.getId();
	}

    private Role createDefaultUserRole() {
        Role role = new Role();
        role.setName("USER_ROLE");
        return roleRepository.save(role);
    }

    private User mapDtoToUser(UserDto userDto, User existingUser) {
        User user = (existingUser != null) ? existingUser : new User();

        modelMapper.getModelMapper().map(userDto, user);

        if (existingUser == null) {
            user.setUsername(userDto.getEmail());
            user.setCreatedAt(Instant.now());
        }

        user.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getRole() != null && !userDto.getRole().isEmpty()) {
            Set<Role> roles = userDto.getRole().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        user.setUpdatedAt(Instant.now());
        return user;
    }


    @Override
    public Page<UserDto> getAllUsers(String search, Pageable pageable) {
        Page<User> users;

        if(search!=null && !search.isBlank()){
            users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, search, pageable);
        } else{
            users = userRepository.findAll(pageable);
        }
        return users.map(this::mapToDto);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .role(
                        user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                )
                .build();
    }

    @Override
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + userId));

        return mapToDto(user);
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email " + email));

        return mapToDto(user);
    }

    @Override
    public UserDto findUserByUsername(String username) {
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
    public UserDto updateUserById(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (!existingUser.getEmail().equals(userDto.getEmail())) {
            userRepository.findByEmail(userDto.getEmail()).ifPresent(user -> {
                throw new ResourceAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
            });
        }

        User updatedUser = mapDtoToUser(userDto, existingUser);
        updatedUser = userRepository.save(updatedUser);

        return mapToDto(updatedUser);
    }

}