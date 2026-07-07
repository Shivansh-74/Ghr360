package com.ghr360.controller;

import com.ghr360.dto.request.DealerPropertyFilterRequest;
import com.ghr360.dto.request.DealerPropertyRequest;
import com.ghr360.dto.response.ApiResponse;
import com.ghr360.dto.response.DashboardResponseDto;
import com.ghr360.dto.response.DealerPropertyResponse;
import com.ghr360.service.DealerPropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class DealerPropertyController {

    private final DealerPropertyService dealerPropertyService;

    /**
     * POST /api/properties/register
     * Multipart form-data: property fields + mandatory thumbnail image.
     * JWT se automatically username nikalega — manually dene ki zarurat nahi.
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DealerPropertyResponse>> registerProperty(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("type")                       String type,
            @RequestParam("ownerName")                  String ownerName,
            @RequestParam(value = "ownerContact", required = false) String ownerContact,
            @RequestParam(value = "locality",     required = false) String locality,
            @RequestParam(value = "address",      required = false) String address,
            @RequestParam(value = "city",         required = false) String city,
            @RequestParam(value = "state",        required = false) String state,
            @RequestParam(value = "dealer",       required = false) String dealer,
            @RequestParam(value = "lat",          required = false) Double lat,
            @RequestParam(value = "longitude",    required = false) Double longitude,
            @RequestParam("price")                      BigDecimal price,
            @RequestParam(value = "squareFeet",   required = false) Double squareFeet,
            @RequestParam(value = "bedrooms",     required = false) Double bedrooms,
            @RequestParam(value = "bathrooms",    required = false) Double bathrooms,
            @RequestParam(value = "parkings",     required = false) Double parkings,
            @RequestParam("thumbnail")                  MultipartFile thumbnail) {

        DealerPropertyRequest request = new DealerPropertyRequest();
        request.setType(type);
        request.setOwnerName(ownerName);
        request.setOwnerContact(ownerContact);
        request.setLocality(locality);
        request.setAddress(address);
        request.setCity(city);
        request.setState(state);
        request.setDealer(dealer);
        request.setLat(lat);
        request.setLongitude(longitude);
        request.setPrice(price);
        request.setSquareFeet(squareFeet);
        request.setBedrooms(bedrooms);
        request.setBathrooms(bathrooms);
        request.setParkings(parkings);

        DealerPropertyResponse response =
                dealerPropertyService.registerProperty(request, thumbnail, authorizationHeader);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Property registered successfully", response));
    }

    /**
     * POST /api/properties/my
     * Sirf logged-in user ki properties aayengi — filter optional hain.
     * Response mein thumbnailUrl bhi include hai.
     *
     * Filter fields (sab optional):
     *   type, ownerName, city, state, locality, minPrice, maxPrice
     */
    @PostMapping("/my")
    public ResponseEntity<ApiResponse<List<DealerPropertyResponse>>> getMyProperties(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) DealerPropertyFilterRequest filter) {

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
                ApiResponse.success("success", data)
        );
    }

    /**
     * PATCH /api/properties/{id}/status
     * Change property status: OPEN | SOLD | FULFILLED
     * SOLD and FULFILLED are excluded from the mobile /my feed.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DealerPropertyResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String status) {

        DealerPropertyResponse response = dealerPropertyService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(
                "Status updated to " + response.getStatus(), response));
    }

    /**
     * DELETE /api/properties/{id}
     * Admin deletes a property. Also removes thumbnail from Cloudinary.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(
            @PathVariable Long id) {

        dealerPropertyService.deleteProperty(id);
        return ResponseEntity.ok(ApiResponse.success("Property deleted successfully"));
    }
}
