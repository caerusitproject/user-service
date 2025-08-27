package com.caerus.userservice.controller;

import java.util.List;
import java.util.Map;

import com.caerus.userservice.payload.SuccessResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
import com.caerus.userservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<SuccessResponse<Map<String, Long>>> save(@Valid @RequestBody UserDto userDto) {
       long id = userService.saveUser(userDto);
        Map<String, Long> responseData = Map.of("userId", id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse<>("User created successfully", responseData));
    }

    @GetMapping

    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.findAllUsers().stream()
                .map(user -> modelMapper.map(user, UserDto.class)).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(modelMapper.map(userService.findUserById(id), UserDto.class));
    }

    @GetMapping("email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(modelMapper.map(userService.findUserByEmail(email), UserDto.class));
    }

    @GetMapping("username/{username}")
    public ResponseEntity<AuthUserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(modelMapper.map(userService.findUserByUsername(username), AuthUserDto.class));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('USER_ROLE') or @iUserService.getUserById(#request.id).username == principal")
    public ResponseEntity<UserDto> updateUserById(@Valid @RequestPart UserDto request,
                                                  @RequestPart(required = false) MultipartFile file) {
        return ResponseEntity.ok(modelMapper.map(userService.findUserById(request.getId()), UserDto.class));
    }

    @DeleteMapping("/{id}")
   // @PreAuthorize("hasRole('ADMIN') or @iUserService.getUserById(#id).username == principal")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }
}