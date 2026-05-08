package com.ghr360.service;


import com.ghr360.config.DealerPropertySpecification;
import com.ghr360.dto.request.DealerPropertyFilterRequest;
import com.ghr360.dto.request.DealerPropertyRequest;
import com.ghr360.dto.response.DashboardResponseDto;
import com.ghr360.dto.response.DealerPropertyResponse;
import com.ghr360.entity.DealerProperty;
import com.ghr360.entity.User;
import com.ghr360.exception.ResourceNotFoundException;
import com.ghr360.repository.DealerPropertyRepository;
import com.ghr360.repository.UserRepository;
import com.ghr360.service.DealerPropertyService;
import com.ghr360.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealerPropertyService {

    private final DealerPropertyRepository dealerPropertyRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // ─── Register Property ───────────────────────────────────────────────────────

    @Transactional
    public DealerPropertyResponse registerProperty(DealerPropertyRequest request, String jwtToken) {

        // JWT se username nikalo
        String username = extractUsername(jwtToken);
        log.info("Registering property for dealer: {}", username);

        // User fetch karo DB se
        User dealer = userRepository.findByUsername(request.getDealer())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dealer not found: " + username));

        // Entity banao
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
                .dealer(dealer)
                .build();

        DealerProperty saved = dealerPropertyRepository.save(property);
        log.info("Property registered with id: {} for dealer: {}", saved.getId(), username);

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
                .build();
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
            return d;
        }).toList();

        dto.setRecentProperties(recentDtos);

        return dto;
    }
}
