package com.ghr360.dto.request;

import com.ghr360.entity.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    private String salutation;

    @NotNull(message = "User type is required")
    private String userType;

    private Double lat;
    private Double longitude;
    private String address;
    private String city;
    private String state;
    private String country;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNo;
    private String alternativeNo;
    private String resourceCode;
}
