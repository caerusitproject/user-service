package com.caerus.userservice.service;
import java.util.List;

import com.caerus.userservice.dto.UserDto;
import com.caerus.userservice.model.User;

public interface UserService {
		
	Long saveUser(UserDto userDto);

    List<UserDto> findAllUsers();

	User findUserByEmail(String email);

	UserDto findUserById(Long userId);

	boolean doesUserExist(Long userId);

	void editUser(UserDto updatedUserDto, Long userId);

	void deleteUserById(Long userId);

	User findUserByUsername(String username);

}