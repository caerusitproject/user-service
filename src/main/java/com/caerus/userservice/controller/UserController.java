package com.caerus.userservice.controller;

import java.util.Map;

import com.caerus.userservice.dto.ApiResponse;
import com.caerus.userservice.dto.RegisterRequest;
import com.caerus.userservice.dto.UserUpdateDto;
import com.caerus.userservice.payload.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.caerus.userservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> save(@Valid @RequestBody RegisterRequest registerRequest) {
        System.out.println(registerRequest.toString());
        Long id = userService.saveUser(registerRequest);
        Map<String, Long> responseData = Map.of("id", id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", responseData));
    }

    @GetMapping
   public ResponseEntity<Page<RegisterRequest>> getAllUsers(
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
    public ResponseEntity<SuccessResponse<RegisterRequest>> getUserById(@PathVariable Long id) {
        RegisterRequest registerRequest = userService.findUserById(id);
        return ResponseEntity.ok(new SuccessResponse<>(registerRequest));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<SuccessResponse<RegisterRequest>> getUserByEmail(@PathVariable String email) {
        RegisterRequest registerRequest = userService.findUserByEmail(email);
        return ResponseEntity.ok(new SuccessResponse<>(registerRequest));
    }

    @GetMapping("username/{username}")
    public ResponseEntity<SuccessResponse<RegisterRequest>> getUserByUsername(@PathVariable String username) {
        RegisterRequest registerRequest = userService.findUserByUsername(username);
        return ResponseEntity.ok(new SuccessResponse<>(registerRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<RegisterRequest>> updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateDto request) {
        return ResponseEntity.ok(new SuccessResponse<>("Data updated successfully", userService.updateUserById(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}