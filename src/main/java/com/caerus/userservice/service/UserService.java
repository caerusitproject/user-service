package com.caerus.userservice.service;

import com.caerus.userservice.dto.RegisterRequest;
import com.caerus.userservice.dto.ResetPasswordRequest;
import com.caerus.userservice.dto.UserRolesDto;
import com.caerus.userservice.dto.UserUpdateDto;
import com.caerus.userservice.payload.SuccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserService {
		
	Long saveUser(RegisterRequest registerRequest);

    Page<RegisterRequest> getAllUsers(String search, Pageable pageable);

	RegisterRequest findUserByEmail(String email);

	RegisterRequest findUserById(Long userId);

	void deleteUserById(Long userId);

	RegisterRequest findUserByUsername(String username);

    RegisterRequest updateUserById(Long userId, UserUpdateDto userUpdateDto);

    UserRolesDto getUserByEmailForInternalService(String email);
}