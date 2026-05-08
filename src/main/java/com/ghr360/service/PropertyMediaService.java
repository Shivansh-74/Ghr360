package com.ghr360.service;

import com.ghr360.dto.response.PropertyMediaResponse;
import com.ghr360.entity.MediaType;
import com.ghr360.entity.PropertyMedia;
import com.ghr360.exception.MediaUploadException;
import com.ghr360.exception.ResourceNotFoundException;
import com.ghr360.repository.DealerPropertyRepository;
import com.ghr360.repository.PropertyMediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyMediaService {

    private final CloudinaryService       cloudinaryService;
    private final PropertyMediaRepository mediaRepository;
    private final DealerPropertyRepository propertyRepository;

    /**
     * Upload one or more files for a property.
     */
    @Transactional
    public List<PropertyMediaResponse> uploadMedia(
            List<MultipartFile> files,
            Long propertyId,
            String userCode,
            MediaType mediaType) {

        // Verify property exists
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        if (files == null || files.isEmpty()) {
            throw new MediaUploadException("No files provided");
        }

        List<PropertyMediaResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            Map<String, String> uploaded = cloudinaryService.upload(file, userCode, propertyId);

            PropertyMedia media = mediaRepository.save(PropertyMedia.builder()
                    .propertyId(propertyId)
                    .userCode(userCode)
                    .mediaType(mediaType)
                    .url(uploaded.get("url"))
                    .publicId(uploaded.get("public_id"))
                    .build());

            responses.add(toResponse(media));
        }

        log.info("Uploaded {} file(s) for propertyId={}, userCode={}", responses.size(), propertyId, userCode);
        return responses;
    }

    /**
     * Get all media for a property, optionally filtered by type.
     */
    public List<PropertyMediaResponse> getByProperty(Long propertyId, MediaType mediaType) {
        List<PropertyMedia> list = (mediaType != null)
                ? mediaRepository.findByPropertyIdAndMediaType(propertyId, mediaType)
                : mediaRepository.findByPropertyId(propertyId);
        return list.stream().map(this::toResponse).toList();
    }

    /**
     * Delete a single media record and remove from Cloudinary.
     */
    @Transactional
    public void deleteMedia(Long mediaId) {
        PropertyMedia media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + mediaId));

        String resourceType = (media.getMediaType() == MediaType.VIDEO) ? "video" : "image";
        cloudinaryService.delete(media.getPublicId(), resourceType);
        mediaRepository.delete(media);

        log.info("Deleted media id={}, publicId={}", mediaId, media.getPublicId());
    }

    // ── Mapper ───────────────────────────────────────────────

    private PropertyMediaResponse toResponse(PropertyMedia m) {
        return PropertyMediaResponse.builder()
                .id(m.getId())
                .propertyId(m.getPropertyId())
                .userCode(m.getUserCode())
                .mediaType(m.getMediaType())
                .url(m.getUrl())
                .publicId(m.getPublicId())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
