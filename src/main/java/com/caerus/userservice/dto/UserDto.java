package com.caerus.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

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

    @NotNull(message = "Password should not be empty")
    private String password;

   
    @NotEmpty(message="Phone number should not be empty")
    @Pattern(regexp = "\\d{10}", message = "Phone number should have exactly 10 numbers")
    private String phone;

    
    private Boolean isActive;
    
    

   	
	private String role;
}