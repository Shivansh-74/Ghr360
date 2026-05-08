package com.ghr360.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ghr360.exception.MediaUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:ghr360}")
    private String baseFolder;

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10 MB

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/quicktime", "video/x-msvideo", "video/webm"
    );

    /**
     * Uploads a file to Cloudinary under ghr360/{userCode}/{propertyId}/
     * Returns a map with "url" and "public_id".
     */
    public Map<String, String> upload(MultipartFile file, String userCode, Long propertyId) {
        validateFile(file);

        String folder       = String.format("%s/%s/%d", baseFolder, userCode, propertyId);
        String resourceType = resolveResourceType(file.getContentType());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",          folder,
                            "resource_type",   resourceType,
                            "use_filename",    true,
                            "unique_filename", true
                    )
            );

            String url      = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            log.info("Uploaded to Cloudinary: publicId={}, url={}", publicId, url);
            return Map.of("url", url, "public_id", publicId);

        } catch (IOException e) {
            log.error("Cloudinary upload failed for property={}, user={}", propertyId, userCode, e);
            throw new MediaUploadException("File upload failed: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a file from Cloudinary by its publicId.
     */
    public void delete(String publicId, String resourceType) {
        try {
            cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", resourceType));
            log.info("Deleted from Cloudinary: publicId={}", publicId);
        } catch (IOException e) {
            log.warn("Failed to delete from Cloudinary: publicId={}", publicId, e);
        }
    }

    // ── Private helpers ──────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new MediaUploadException("File must not be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new MediaUploadException("File size exceeds 10MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!ALLOWED_IMAGE_TYPES.contains(contentType) && !ALLOWED_VIDEO_TYPES.contains(contentType))) {
            throw new MediaUploadException(
                    "Unsupported file type: " + contentType +
                    ". Allowed: JPEG, PNG, WEBP, GIF, MP4, MOV, AVI, WEBM");
        }
    }

    public String resolveResourceType(String contentType) {
        if (contentType != null && contentType.startsWith("video/")) return "video";
        return "image";
    }
}
