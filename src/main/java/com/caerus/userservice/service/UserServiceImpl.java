package com.caerus.userservice.service;

import com.caerus.userservice.enums.RoleType;
import com.caerus.userservice.exception.ResourceAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.caerus.userservice.dto.UserDto;
import com.caerus.userservice.exception.NotFoundException;
import com.caerus.userservice.model.Role;
import com.caerus.userservice.model.User;
import com.caerus.userservice.repository.RoleRepository;
import com.caerus.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;

	@Override
	public Long saveUser(UserDto userDto) {

        userRepository.findByEmail(userDto.getEmail()).ifPresent(user -> {
            throw new ResourceAlreadyExistsException("User with email " + userDto.getEmail()+ " already exists");
        });

        Role role = roleRepository.findByName(RoleType.USER_ROLE.name())
                .orElseGet(this::createDefaultUserRole);

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIsActive(false);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setRoles(Collections.singleton(role));

        User savedUser = userRepository.save(user);

        return savedUser.getId();
	}

    private Role createDefaultUserRole() {
        Role role = new Role();
        role.setName("USER_ROLE");
        return roleRepository.save(role);
    }

	public void deleteUserById(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		userOptional.ifPresent(user -> {
			user.getRoles().clear();
			userRepository.delete(user);
		});
	}

	public boolean doesUserExist(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		return userOptional.isPresent();
	}

	/*
	 * public User getUserByUsername(String username) { return
	 * userRepository.findByEmail(username) .orElseThrow(() -> new
	 * NotFoundException("User not found with username: " + username)); }
	 */

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User Email not found"));
	}

	@Override
	public UserDto findUserById(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);

		if (userOptional.isPresent()) {
			return mapToUserDto(userOptional.get());
		}
		return null;
	}

	public void editUser(UserDto updatedUserDto, Long userId) {
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		modelMapper.map(updatedUserDto, existingUser);
		// Update password only if it's provided in the DTO

		if (!updatedUserDto.getPassword().isEmpty()) {
			existingUser.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
		}
		userRepository.save(existingUser);
	}

	@Override
	public List<UserDto> findAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().map((user) -> mapToUserDto(user)).collect(Collectors.toList());
	}

	private UserDto mapToUserDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setFirstName(user.getFirstName());

		userDto.setLastName(user.getLastName());
		userDto.setEmail(user.getEmail());
		userDto.setPhone(user.getPhone());
		//userDto.setRole(ERole.valueOf(user.getRoles().iterator().next().getName()).name());
		return userDto;
	}

	private Role checkRoleExist() {
		Role role = new Role();
		role.setName("USER_ROLE");
		return roleRepository.save(role);
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findByEmail(username)
				.orElseThrow(() -> new NotFoundException("User not found with username: " + username));
	}

	public User updateUserById(UserDto request, MultipartFile file) {
		User toUpdate = userRepository.findById(request.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getId()));

		// request.setUserDetails(updateUserDetails(toUpdate.getUserDetails(),
		// request.getUserDetails(), file));
		modelMapper.map(request, User.class);

		return userRepository.save(toUpdate);
	}

}