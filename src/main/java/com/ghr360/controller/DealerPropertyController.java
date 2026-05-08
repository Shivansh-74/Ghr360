package com.ghr360.controller;

import com.ghr360.dto.request.DealerPropertyFilterRequest;
import com.ghr360.dto.request.DealerPropertyRequest;
import com.ghr360.dto.response.ApiResponse;
import com.ghr360.dto.response.DashboardResponseDto;
import com.ghr360.dto.response.DealerPropertyResponse;
import com.ghr360.entity.DealerProperty;
import com.ghr360.service.DealerPropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class DealerPropertyController {

    private final DealerPropertyService dealerPropertyService;

    /**
     * POST /api/properties/register
     * Koi bhi authenticated user apni property register kar sakta hai.
     * JWT se automatically username nikalega — manually dene ki zarurat nahi.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DealerPropertyResponse>> registerProperty(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody DealerPropertyRequest request) {

        DealerPropertyResponse response =
                dealerPropertyService.registerProperty(request, authorizationHeader);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Property registered successfully", response));
    }

    /**
     * POST /api/properties/my
     * Sirf logged-in user ki properties aayengi — filter optional hain.
     *
     * Filter fields (sab optional):
     *   type, ownerName, city, state, locality, minPrice, maxPrice
     */
    @PostMapping("/my")
    public ResponseEntity<ApiResponse<List<DealerPropertyResponse>>> getMyProperties(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) DealerPropertyFilterRequest filter) {

        // Agar body send nahi ki to empty filter use karo
        if (filter == null) {
            filter = new DealerPropertyFilterRequest();
        }

        List<DealerPropertyResponse> properties =
                dealerPropertyService.getMyProperties(authorizationHeader, filter);

        return ResponseEntity.ok(
                ApiResponse.success("Properties fetched successfully", properties));
    }
    
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {

        DashboardResponseDto data = dealerPropertyService.getDashboardData();

        return ResponseEntity.ok(
        		ApiResponse.success(
                "success", data
            )
        );
    }
}
