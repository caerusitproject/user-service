package com.caerus.userservice.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.caerus.userservice.dto.UserDto;
import com.caerus.userservice.exception.NotFoundException;
import com.caerus.userservice.model.ERole;
import com.caerus.userservice.model.Role;
import com.caerus.userservice.model.User;
import com.caerus.userservice.repository.RoleRepository;
import com.caerus.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
       user.setFirstName(userDto.getFirstName());
       user.setLastName(userDto.getLastName());
       user.setEmail(userDto.getEmail());
       user.setPassword(passwordEncoder.encode(userDto.getPassword()));
       user.setPhone(userDto.getPhone());
       user.setIsActive(false);
       user.setCreatedAt(LocalDateTime.now());
       Role role = roleRepository.findByName("USER_ROLE").get();
       role = checkRoleExist();
       user.setRoles(null);
       userRepository.save(user);
       
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
    
    public User getUserByUsername(String username) {
        return userRepository.findByEmail(username)
				.orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }

   

	@Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User Email not found"));
    }

    public UserDto findUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        		
        if(userOptional.isPresent()){
            return mapToUserDto(userOptional.get());
        }
        return null;
    }

    public void editUser(UserDto updatedUserDto, Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
		/*
		 * existingUser.setFirstName(updatedUserDto.getFirstName());
		 * existingUser.setLastName(updatedUserDto.getLastName());
		 * existingUser.setEmail(updatedUserDto.getEmail());
		 * existingUser.setPhone(updatedUserDto.getPhone());
		 * existingUser.setIsActive(updatedUserDto.getIsActive());
		 */
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
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setRole(ERole.valueOf(user.getRoles().iterator().next().getName()).name());
        return userDto;
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("USER_ROLE");
        return roleRepository.save(role);
    }

	
}