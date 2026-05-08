package com.ghr360.service;

import com.ghr360.dto.request.LoginRequest;
import com.ghr360.dto.request.RegisterRequest;
import com.ghr360.dto.response.LoginResponse;
import com.ghr360.dto.response.UserResponse;
import com.ghr360.entity.BlacklistedToken;
import com.ghr360.entity.User;
import com.ghr360.exception.UserAlreadyExistsException;
import com.ghr360.repository.BlacklistedTokenRepository;
import com.ghr360.repository.UserRepository;
import com.ghr360.security.CustomUserDetails;
import com.ghr360.service.AuthService;
import com.ghr360.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService{

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        // Mark first time login as false after first successful login
        User user = userDetails.getUser();
        if (Boolean.TRUE.equals(user.getIsFirstTimeLogin())) {
            user.setIsFirstTimeLogin(false);
            userRepository.save(user);
        }

        log.info("User logged in successfully: {}", request.getUsername());
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationInMillis())

                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .salutation(user.getSalutation())

                .lat(user.getLat())
                .longitude(user.getLongitude())

                .userType(user.getUserType())
                .isFirstTimeLogin(user.getIsFirstTimeLogin())
                .isActive(user.getIsActive())

                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .country(user.getCountry())

                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .alternativeNo(user.getAlternativeNo())

                .resourceCode(user.getResourceCode())

                .build();
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Username already taken: " + request.getUsername());
        }

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .salutation(request.getSalutation())
                .userType(request.getUserType())
                .lat(request.getLat())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .email(request.getEmail())
                .phoneNo(request.getPhoneNo())
                .alternativeNo(request.getAlternativeNo())
                .resourceCode(request.getResourceCode())
                .isFirstTimeLogin(true)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", request.getUsername());

        return mapToUserResponse(user);
    }

    @Transactional
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isBlank()) {
            return;
        }

        // Persist to blacklist so the filter can block it
        if (!blacklistedTokenRepository.existsByToken(token)) {
            Instant expiresAt = jwtUtil.getExpirationDateFromToken(token).toInstant();
            BlacklistedToken blacklisted = BlacklistedToken.builder()
                    .token(token)
                    .blacklistedAt(Instant.now())
                    .expiresAt(expiresAt)
                    .build();
            blacklistedTokenRepository.save(blacklisted);
            log.info("Token blacklisted (user logged out)");
        }
    }

    // ─── Mapper ─────────────────────────────────────────────────────────────────

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .salutation(user.getSalutation())
                .lat(user.getLat())
                .longitude(user.getLongitude())
                .userType(user.getUserType())
                .isFirstTimeLogin(user.getIsFirstTimeLogin())
                .isActive(user.getIsActive())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .country(user.getCountry())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .alternativeNo(user.getAlternativeNo())
                .resourceCode(user.getResourceCode())
                .build();
    }
}
