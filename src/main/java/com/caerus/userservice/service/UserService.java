package com.caerus.userservice.service;

import com.caerus.userservice.dto.RegisterRequest;
import com.caerus.userservice.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
		
	Long saveUser(RegisterRequest registerRequest);

    Page<RegisterRequest> getAllUsers(String search, Pageable pageable);

	RegisterRequest findUserByEmail(String email);

	RegisterRequest findUserById(Long userId);

	void deleteUserById(Long userId);

	RegisterRequest findUserByUsername(String username);

    RegisterRequest updateUserById(Long userId, UserUpdateDto userUpdateDto);

}