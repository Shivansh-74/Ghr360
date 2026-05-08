package com.ghr360.controller;

import com.ghr360.dto.response.ApiResponse;
import com.ghr360.dto.response.UserResponse;
import com.ghr360.security.CustomUserDetails;
import com.ghr360.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me
     * Any authenticated user — returns the caller's own profile.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        UserResponse response = userService.getUserByUsername(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User profile fetched", response));
    }

    /**
     * GET /api/users
     * ADMIN only — returns all registered users.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users fetched", users));
    }

    /**
     * GET /api/users/{username}
     * ADMIN only — fetch a specific user by username.
     */
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(
            @PathVariable String username) {

        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User fetched", user));
    }

    /**
     * PATCH /api/users/{userId}/deactivate
     * ADMIN only — soft-deactivates a user account.
     */
    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long userId) {

        userService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }

    /**
     * GET /api/users/ping
     * Any authenticated user — simple JWT validation smoke test.
     */
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        String message = String.format("pong — authenticated as %s [%s]",
                currentUser.getUsername(),
                currentUser.getUser().getUserType());
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    @GetMapping("/getUserMap")
    public Map<String,String> getUserMap(){
    	   return userService.getUserMap();
    }
}
