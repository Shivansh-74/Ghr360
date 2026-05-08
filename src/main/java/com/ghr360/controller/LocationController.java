package com.ghr360.controller;

import com.ghr360.dto.request.LocationRequest;
import com.ghr360.dto.response.ApiResponse;
import com.ghr360.dto.response.LocationResponse;
import com.ghr360.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // POST /api/location/add  — ADMIN only
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LocationResponse>> add(
            @Valid @RequestBody LocationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Location added", locationService.add(req)));
    }

    // PUT /api/location/update/{id}  — ADMIN only
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LocationResponse>> update(
            @PathVariable Long id, @RequestBody LocationRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Location updated", locationService.update(id, req)));
    }

    // PATCH /api/location/deactivate/{id}  — ADMIN only
    @PatchMapping("/deactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        locationService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Location deactivated"));
    }

    // GET /api/location/states  — public
    @GetMapping("/states")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getStates() {
        return ResponseEntity.ok(ApiResponse.success("States fetched", locationService.getStates()));
    }

    // GET /api/location/cities/{stateId}  — public (legacy, by id)
    @GetMapping("/cities/{stateId}")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getCitiesByState(
            @PathVariable Long stateId) {
        return ResponseEntity.ok(ApiResponse.success("Cities fetched",
                locationService.getCitiesByState(stateId)));
    }

    // GET /api/location/cities/by-code/{stateCode}  — public (preferred)
    @GetMapping("/cities/by-code/{stateCode}")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getCitiesByStateCode(
            @PathVariable String stateCode) {
        return ResponseEntity.ok(ApiResponse.success("Cities fetched",
                locationService.getCitiesByStateCode(stateCode)));
    }

    // GET /api/location/all  — public
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("All locations fetched", locationService.getAll()));
    }

    // GET /api/location/map/states  — public; returns { "MP": "Madhya Pradesh", ... }
    @GetMapping("/map/states")
    public ResponseEntity<ApiResponse<Map<String, String>>> getStatesMap() {
        return ResponseEntity.ok(ApiResponse.success("States map fetched", locationService.getStatesMap()));
    }

    // GET /api/location/map/cities/{stateCode}  — public; returns { "BPL": "Bhopal", ... }
    @GetMapping("/map/cities/{stateCode}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getCitiesMap(
            @PathVariable String stateCode) {
        return ResponseEntity.ok(ApiResponse.success("Cities map fetched",
                locationService.getCitiesMap(stateCode)));
    }
}
