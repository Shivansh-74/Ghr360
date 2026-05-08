package com.ghr360.controller;

import com.ghr360.dto.request.MasterRequest;
import com.ghr360.dto.response.ApiResponse;
import com.ghr360.dto.response.MasterResponse;
import com.ghr360.entity.CityMst;
import com.ghr360.exception.ResourceNotFoundException;
import com.ghr360.repository.CityMstRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@RequiredArgsConstructor
public class CityMstController {

    private final CityMstRepository cityMstRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MasterResponse>> add(@RequestBody MasterRequest req) {
        CityMst city = cityMstRepository.save(
                CityMst.builder().name(req.getName()).build());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("City added", toResponse(city)));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MasterResponse>> update(
            @PathVariable Long id, @RequestBody MasterRequest req) {
        CityMst city = cityMstRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
        if (req.getName() != null) city.setName(req.getName());
        if (Boolean.FALSE.equals(req.getIsActive())) city.setIsActive(false);
        return ResponseEntity.ok(ApiResponse.success("City updated", toResponse(cityMstRepository.save(city))));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MasterResponse>>> getAll() {
        List<MasterResponse> list = cityMstRepository.findAllByOrderByNameAsc()
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("Cities fetched", list));
    }

    private MasterResponse toResponse(CityMst c) {
        return MasterResponse.builder().id(c.getId()).name(c.getName()).isActive(c.getIsActive()).build();
    }
}
