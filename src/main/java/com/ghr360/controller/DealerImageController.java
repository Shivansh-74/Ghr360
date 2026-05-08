package com.ghr360.controller;

import com.ghr360.dto.response.ApiResponse;
import com.ghr360.dto.response.PropertyMediaResponse;
import com.ghr360.entity.MediaType;
import com.ghr360.service.PropertyMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class DealerImageController {

    private final PropertyMediaService mediaService;

    @PostMapping("/upload/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PropertyMediaResponse>>> upload(
            @PathVariable Long propertyId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userCode") String userCode) {

        List<PropertyMediaResponse> result = mediaService.uploadMedia(
                List.of(file), propertyId, userCode, MediaType.IMAGE);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded", result));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<List<PropertyMediaResponse>>> getByProperty(
            @PathVariable Long propertyId) {

        List<PropertyMediaResponse> images = mediaService.getByProperty(propertyId, MediaType.IMAGE);
        return ResponseEntity.ok(ApiResponse.success("Images fetched", images));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long imageId) {
        mediaService.deleteMedia(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted"));
    }
}
