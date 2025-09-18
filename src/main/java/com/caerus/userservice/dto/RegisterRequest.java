package com.caerus.userservice.dto;



import com.caerus.userservice.validation.StrongPassword;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private Long id;

    @NotBlank(message = "First name should not be empty")
    @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
    private String firstName;

    @NotEmpty(message = "Last name should not be empty")
    private String lastName;

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password is required")
    @StrongPassword
    private String password;


    @Size(max= 4, message = "Country code must be 1 to 4 digits")
    @NotBlank(message = "Country code should not be empty")
    private String countryCode;

    @Pattern(regexp = "\\d{4,12}", message = "Phone number must be 4 to 12 digits")
    @NotBlank(message = "Phone number should not be empty")
    private String phoneNumber;

    private Boolean isActive;
    private Set<String> role;
    private Instant createdAt;
    private Instant updatedAt;
}
