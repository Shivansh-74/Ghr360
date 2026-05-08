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
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class PropertyMediaController {

    private final PropertyMediaService mediaService;

    /**
     * POST /api/media/upload
     * Accepts multiple files, propertyId, userCode, mediaType.
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PropertyMediaResponse>>> upload(
            @RequestParam("files")      List<MultipartFile> files,
            @RequestParam("propertyId") Long propertyId,
            @RequestParam("userCode")   String userCode,
            @RequestParam(value = "mediaType", defaultValue = "IMAGE") MediaType mediaType) {

        List<PropertyMediaResponse> result = mediaService.uploadMedia(files, propertyId, userCode, mediaType);
        return ResponseEntity.ok(ApiResponse.success(
                result.size() + " file(s) uploaded successfully", result));
    }

    /**
     * GET /api/media/{propertyId}
     * Fetch all media for a property. Optional ?mediaType=IMAGE|VIDEO|VR360
     */
    @GetMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<List<PropertyMediaResponse>>> getByProperty(
            @PathVariable Long propertyId,
            @RequestParam(required = false) MediaType mediaType) {

        List<PropertyMediaResponse> result = mediaService.getByProperty(propertyId, mediaType);
        return ResponseEntity.ok(ApiResponse.success("Media fetched", result));
    }

    /**
     * DELETE /api/media/{mediaId}
     * Deletes from Cloudinary and DB.
     */
    @DeleteMapping("/{mediaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long mediaId) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.ok(ApiResponse.success("Media deleted successfully"));
    }
}
