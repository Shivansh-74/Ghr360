package com.ghr360.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_TABLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "FIRSTNAME", length = 100)
    private String firstname;

    @Column(name = "LASTNAME", length = 100)
    private String lastname;

    @Column(name = "SALUTATION", length = 20)
    private String salutation;

    @Column(name = "LAT")
    private Double lat;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name = "USERTYPE", nullable = false, length = 20)
    private String userType;

    @Column(name = "IS_FIRST_TIME_LOGIN")
    private Boolean isFirstTimeLogin = true;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "STATE", length = 100)
    private String state;

    @Column(name = "COUNTRY", length = 100)
    private String country;

    @Column(name = "EMAIL", unique = true, length = 150)
    private String email;

    @Column(name = "PHONE_NO", length = 20)
    private String phoneNo;

    @Column(name = "ALTERNATIVE_NO", length = 20)
    private String alternativeNo;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "RESOURCE_CODE", length = 100)
    private String resourceCode;
}