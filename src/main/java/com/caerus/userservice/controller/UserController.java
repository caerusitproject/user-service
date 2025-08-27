package com.caerus.userservice.controller;

import java.util.List;
import java.util.Map;

import com.caerus.userservice.payload.SuccessResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
   public ResponseEntity<Page<UserDto>> getAllUsers(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "10") int size,
           @RequestParam(defaultValue = "id") String sortBy,
           @RequestParam(defaultValue = "asc") String sortOrder,
           @RequestParam(required = false) String search
    ){
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(userService.getAllUsers(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.findUserById(id);
        return ResponseEntity.ok(new SuccessResponse<>(userDto));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<SuccessResponse<UserDto>> getUserByEmail(@PathVariable String email) {
        UserDto userDto = userService.findUserByEmail(email);
        return ResponseEntity.ok(new SuccessResponse<>(userDto));
    }

    @GetMapping("username/{username}")
    public ResponseEntity<SuccessResponse<UserDto>> getUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.findUserByUsername(username);
        return ResponseEntity.ok(new SuccessResponse<>(userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<UserDto>> updateUserById(@PathVariable Long id, @Valid @RequestBody UserDto request) {
        return ResponseEntity.ok(new SuccessResponse<>("Data updated successfully", userService.updateUserById(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}