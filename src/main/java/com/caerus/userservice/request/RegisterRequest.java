package com.caerus.userservice.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Firestname is required")
    private String firstName;
    @NotBlank(message = "Firestname is required")
    private String lastName;
    @Email(message = "Email should be valid")
    private String email;
   
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters and contain at least one letter and one number")
    @NotNull(message = "Password is required")
    private String password;
    
    private Boolean isActive;
    
   	private String role;
   
}