package com.ghr360.service;


import com.ghr360.config.DealerPropertySpecification;
import com.ghr360.dto.request.DealerPropertyFilterRequest;
import com.ghr360.dto.request.DealerPropertyRequest;
import com.ghr360.dto.response.DashboardResponseDto;
import com.ghr360.dto.response.DealerPropertyResponse;
import com.ghr360.entity.DealerProperty;
import com.ghr360.entity.User;
import com.ghr360.exception.MediaUploadException;
import com.ghr360.exception.ResourceNotFoundException;
import com.ghr360.repository.DealerPropertyRepository;
import com.ghr360.repository.PropertyMediaRepository;
import com.ghr360.repository.UserRepository;
import com.ghr360.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealerPropertyService {

    private final DealerPropertyRepository dealerPropertyRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CloudinaryService cloudinaryService;
    private final PropertyMediaRepository propertyMediaRepository;

    // ─── Register Property ───────────────────────────────────────────────────────

    @Transactional
    public DealerPropertyResponse registerProperty(DealerPropertyRequest request,
                                                    MultipartFile thumbnail,
                                                    String jwtToken) {

        // JWT se username nikalo
        String username = extractUsername(jwtToken);
        log.info("Registering property for dealer: {}", username);

        // Thumbnail mandatory validation
        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new MediaUploadException("Thumbnail image is required");
        }

        // User fetch karo DB se
        User dealer = userRepository.findByUsername(request.getDealer())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dealer not found: " + request.getDealer()));

        // First save the property (without thumbnail) to get an ID for the Cloudinary folder
        DealerProperty property = DealerProperty.builder()
                .type(request.getType())
                .ownerName(request.getOwnerName())
                .ownerContact(request.getOwnerContact())
                .locality(request.getLocality())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .lat(request.getLat())
                .longitude(request.getLongitude())
                .price(request.getPrice())
                .squareFeet(request.getSquareFeet())
                .bedrooms(request.getBedrooms())
                .bathrooms(request.getBathrooms())
                .parkings(request.getParkings())
                .status("OPEN")
                .dealer(dealer)
                .build();

        DealerProperty saved = dealerPropertyRepository.save(property);

        // Upload thumbnail to Cloudinary under ghr360/{dealerUsername}/{propertyId}/
        Map<String, String> uploaded = cloudinaryService.upload(thumbnail, dealer.getUsername(), saved.getId());
        saved.setThumbnailUrl(uploaded.get("url"));
        saved.setThumbnailPublicId(uploaded.get("public_id"));
        saved = dealerPropertyRepository.save(saved);

        log.info("Property registered with id: {} for dealer: {}, thumbnailUrl: {}",
                saved.getId(), username, saved.getThumbnailUrl());

        return mapToResponse(saved);
    }

    // ─── Get Properties with Filters (admin sees all, dealer sees own) ──────────

    public List<DealerPropertyResponse> getMyProperties(String jwtToken,
                                                         DealerPropertyFilterRequest filter) {
        String usernameFilter = null;

        if (!"Y".equalsIgnoreCase(filter.getIsAdmin())) {
            // Dealer mode: extract username from JWT
            usernameFilter = extractUsername(jwtToken);
            log.info("Fetching properties for dealer: {} with filters", usernameFilter);
        } else {
            log.info("Admin mode: fetching ALL properties with filters");
        }
       // System.out.println(usernameFilter);
        Specification<DealerProperty> spec =
                DealerPropertySpecification.filterBy(usernameFilter, filter);

        List<DealerProperty> properties = dealerPropertyRepository.findAll(spec);
        log.info("Found {} properties", properties.size());

        return properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Helper: JWT se username nikalo ─────────────────────────────────────────

    private String extractUsername(String jwtToken) {
        // "Bearer eyJ..." format handle karo
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }
        return jwtUtil.extractUsername(jwtToken);
    }

    // ─── Mapper ─────────────────────────────────────────────────────────────────

    private DealerPropertyResponse mapToResponse(DealerProperty property) {
        return DealerPropertyResponse.builder()
                .id(property.getId())
                .type(property.getType())
                .ownerName(property.getOwnerName())
                .ownerContact(property.getOwnerContact())
                .locality(property.getLocality())
                .address(property.getAddress())
                .city(property.getCity())
                .state(property.getState())
                .lat(property.getLat())
                .longitude(property.getLongitude())
                .price(property.getPrice())
                .dealerCode(property.getDealer().getUsername())
                .thumbnailUrl(property.getThumbnailUrl())
                .squareFeet(property.getSquareFeet())
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .parkings(property.getParkings())
                .status(property.getStatus())
                .build();
    }
    
    // ─── Update Property Status ──────────────────────────────────────────────────

    private static final List<String> VALID_STATUSES = List.of("OPEN", "SOLD", "FULFILLED");

    @Transactional
    public DealerPropertyResponse updateStatus(Long id, String status) {
        String normalized = status != null ? status.toUpperCase().trim() : "";
        if (!VALID_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Invalid status '" + status + "'. Allowed values: OPEN, SOLD, FULFILLED");
        }

        DealerProperty property = dealerPropertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));

        property.setStatus(normalized);
        DealerProperty saved = dealerPropertyRepository.save(property);
        log.info("Property {} status changed to {}", id, normalized);
        return mapToResponse(saved);
    }

    // ─── Delete Property ─────────────────────────────────────────────────────────

    @Transactional
    public void deleteProperty(Long id) {
        DealerProperty property = dealerPropertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));

        // Delete all associated media files from Cloudinary
        propertyMediaRepository.findByPropertyId(id).forEach(media -> {
            String resourceType = "video".equals(
                    media.getMediaType() != null ? media.getMediaType().name().toLowerCase() : "")
                    ? "video" : "image";
            try {
                cloudinaryService.delete(media.getPublicId(), resourceType);
            } catch (Exception ex) {
                log.warn("Could not delete media {} from Cloudinary: {}", media.getPublicId(), ex.getMessage());
            }
        });
        propertyMediaRepository.deleteByPropertyId(id);

        // Delete thumbnail from Cloudinary
        if (property.getThumbnailPublicId() != null && !property.getThumbnailPublicId().isBlank()) {
            try {
                cloudinaryService.delete(property.getThumbnailPublicId(), "image");
            } catch (Exception ex) {
                log.warn("Could not delete thumbnail {} from Cloudinary: {}", property.getThumbnailPublicId(), ex.getMessage());
            }
        }

        dealerPropertyRepository.delete(property);
        log.info("Property {} deleted successfully", id);
    }

    public DashboardResponseDto getDashboardData() {
        DashboardResponseDto dto = new DashboardResponseDto();

        dto.setTotalProperties(dealerPropertyRepository.countAllProperties());
        dto.setRentCount(dealerPropertyRepository.countRent());
        dto.setSaleCount(dealerPropertyRepository.countSale());
        dto.setTotalDealers(dealerPropertyRepository.countDealers());

        List<DealerProperty> recent = dealerPropertyRepository.findRecent(PageRequest.of(0, 5));

        List<DealerPropertyResponse> recentDtos = recent.stream().map(p -> {
        	DealerPropertyResponse d = new DealerPropertyResponse();
            d.setId(p.getId());
            d.setOwnerName(p.getOwnerName());
            d.setType(p.getType());
            d.setCity(p.getCity());
            d.setPrice(p.getPrice());
            d.setDealerCode(p.getDealer().getFirstname());
            d.setThumbnailUrl(p.getThumbnailUrl());
            return d;
        }).toList();

        dto.setRecentProperties(recentDtos);

        return dto;
    }
}
