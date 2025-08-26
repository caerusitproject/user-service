package com.caerus.userservice.service;
import java.util.List;

import com.caerus.userservice.dto.UserDto;
import com.caerus.userservice.model.User;
import com.caerus.userservice.request.RegisterRequest;

public interface IUserService {
		
	void saveUser(UserDto userDto);

    List<UserDto> findAllUsers();

	User findUserByEmail(String email);

	UserDto findUserById(Long userId);

	boolean doesUserExist(Long userId);

	void editUser(UserDto updatedUserDto, Long userId);

	void deleteUserById(Long userId);

	User findUserByUsername(String username);

}