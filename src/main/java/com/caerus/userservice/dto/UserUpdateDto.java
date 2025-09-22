package com.caerus.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {

    @NotBlank(message = "First name should not be empty")
    private String firstName;

    @NotEmpty(message = "Last name should not be empty")
    private String lastName;

    @Email
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters, contain at least one uppercase letter and one number"
    )
    private String password;

    @Pattern(regexp = "\\d{10}", message = "Phone number should have exactly 10 digits")
    private String phone;

    private Boolean isActive;

    private Set<String> roles;
}
