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

    @Pattern(regexp = "\\d{10}", message = "Phone number should have exactly 10 digits")
    private String phone;

    private Boolean isActive;

    private Set<String> roles;
}
