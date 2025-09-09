package com.caerus.userservice.dto;

import com.caerus.userservice.domain.Role;

import lombok.Data;

@Data
public class AuthUserDto {
    private String id;
    private String username;
    private String password;
    private Role role;
}