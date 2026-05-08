package com.ghr360.controller;

import com.ghr360.dto.request.MasterRequest;
import com.ghr360.dto.response.ApiResponse;
import com.ghr360.dto.response.MasterResponse;
import com.ghr360.entity.StateMst;
import com.ghr360.exception.ResourceNotFoundException;
import com.ghr360.repository.StateMstRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/state")
@RequiredArgsConstructor
public class StateMstController {

    private final StateMstRepository stateMstRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MasterResponse>> add(@RequestBody MasterRequest req) {
        StateMst state = stateMstRepository.save(
                StateMst.builder().name(req.getName()).build());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("State added", toResponse(state)));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MasterResponse>> update(
            @PathVariable Long id, @RequestBody MasterRequest req) {
        StateMst state = stateMstRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found: " + id));
        if (req.getName() != null) state.setName(req.getName());
        if (Boolean.FALSE.equals(req.getIsActive())) state.setIsActive(false);
        return ResponseEntity.ok(ApiResponse.success("State updated", toResponse(stateMstRepository.save(state))));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MasterResponse>>> getAll() {
        List<MasterResponse> list = stateMstRepository.findAllByOrderByNameAsc()
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("States fetched", list));
    }

    private MasterResponse toResponse(StateMst s) {
        return MasterResponse.builder().id(s.getId()).name(s.getName()).isActive(s.getIsActive()).build();
    }
}
