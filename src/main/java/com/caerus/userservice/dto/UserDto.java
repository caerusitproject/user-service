package com.caerus.userservice.dto;



import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto
{
    private Long id;

    @NotBlank(message = "First name should not be empty")
    @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
    private String firstName;

    @NotEmpty(message = "Last name should not be empty")
    private String lastName;

    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    
   
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
    message = "Password must be at least 8 characters and contain at least one capital letter and one number")
    @NotNull(message = "Password is required")
    private String password;

   
    @NotEmpty(message="Phone number should not be empty")
    @Pattern(regexp = "\\d{10}", message = "Phone number should have exactly 10 numbers")
    private String phone;

    private String username;

    private Boolean isActive;
    
	private Set<String> role;

    private Instant createdAt;

    private Instant updatedAt;
}