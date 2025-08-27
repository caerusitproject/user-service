package com.caerus.userservice.service;

import com.caerus.userservice.dto.UserDto;
import com.caerus.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
		
	Long saveUser(UserDto userDto);

    Page<UserDto> getAllUsers(String search, Pageable pageable);

	UserDto findUserByEmail(String email);

	UserDto findUserById(Long userId);

	void deleteUserById(Long userId);

	UserDto findUserByUsername(String username);

    UserDto updateUserById(Long userId, UserDto userDto);

}