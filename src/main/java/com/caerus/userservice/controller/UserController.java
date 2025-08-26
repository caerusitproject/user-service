package com.caerus.userservice.controller;

import java.util.List;

import org.apache.camel.language.bean.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.caerus.userservice.dto.AuthUserDto;
import com.caerus.userservice.dto.UserDto;
import com.caerus.userservice.service.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService iUserService;
    @Bean(ref = "")
    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> save(@Valid @RequestBody UserDto request) {
        return ResponseEntity.ok(modelMapper.map(iUserService.findUserById(request.getId()), UserDto.class));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('USER_ROLE')")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(iUserService.findAllUsers().stream()
                .map(user -> modelMapper.map(user, UserDto.class)).toList());
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(modelMapper.map(iUserService.findUserById(id), UserDto.class));
    }

    @GetMapping("/getUserByEmail/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(modelMapper.map(iUserService.findUserByEmail(email), UserDto.class));
    }

    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<AuthUserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(modelMapper.map(iUserService.findUserByUsername(username), AuthUserDto.class));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER_ROLE') or @iUserService.getUserById(#request.id).username == principal")
    public ResponseEntity<UserDto> updateUserById(@Valid @RequestPart UserDto request,
                                                  @RequestPart(required = false) MultipartFile file) {
        return ResponseEntity.ok(modelMapper.map(iUserService.findUserById(request.getId()), UserDto.class));
    }

    @DeleteMapping("/deleteUserById/{id}")
    @PreAuthorize("hasRole('ADMIN') or @iUserService.getUserById(#id).username == principal")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        iUserService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }
}