package com.ghr360.dto.response;

import com.ghr360.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String salutation;
    private Double lat;
    private Double longitude;
    private String userType;
    private Boolean isFirstTimeLogin;
    private Boolean isActive;
    private String address;
    private String city;
    private String state;
    private String country;
    private String email;
    private String phoneNo;
    private String alternativeNo;
    private String resourceCode;
}
