package com.ghr360.service;

import com.ghr360.dto.request.LocationRequest;
import com.ghr360.dto.response.LocationResponse;
import com.ghr360.entity.LocationMst;
import com.ghr360.exception.ResourceNotFoundException;
import com.ghr360.repository.LocationMstRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationMstRepository locationMstRepository;

    // ── Add ──────────────────────────────────────────────────────────────────────

    @Transactional
    public LocationResponse add(LocationRequest req) {

        String type = req.getType().toUpperCase().trim();
        String name = req.getName().trim();
        String code = req.getCode() != null ? req.getCode().toUpperCase().trim() : null;

        // STATE: code is required
        if ("STATE".equals(type)) {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("Code is required for STATE (e.g. MP, MH, DL)");
            }
            if (locationMstRepository.existsByCodeIgnoreCase(code)) {
                throw new IllegalArgumentException("State code '" + code + "' already exists");
            }

            LocationMst state = LocationMst.builder()
                    .type("STATE")
                    .code(code)
                    .name(name)
                    .parentId(null)
                    .parentCode(null)
                    .isActive(true)
                    .build();

            return toResponse(locationMstRepository.save(state));
        }

        // CITY: resolve parent state via parentCode or parentId
        if ("CITY".equals(type)) {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("Code is required for CITY (e.g. BPL, IND, MUM)");
            }
            if (locationMstRepository.existsByCodeIgnoreCase(code)) {
                throw new IllegalArgumentException("City code '" + code + "' already exists");
            }

            LocationMst parentState = resolveParentState(req);

            LocationMst city = LocationMst.builder()
                    .type("CITY")
                    .code(code)
                    .name(name)
                    .parentId(parentState.getId())
                    .parentCode(parentState.getCode())
                    .isActive(true)
                    .build();

            return toResponse(locationMstRepository.save(city));
        }

        throw new IllegalArgumentException("Type must be STATE or CITY");
    }

    // ── Update ───────────────────────────────────────────────────────────────────

    @Transactional
    public LocationResponse update(Long id, LocationRequest req) {

        LocationMst location = locationMstRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found: " + id));

        if (req.getName() != null) {
            location.setName(req.getName().trim());
        }

        if (req.getCode() != null) {
            String newCode = req.getCode().toUpperCase().trim();
            if (!newCode.equals(location.getCode()) && locationMstRepository.existsByCodeIgnoreCase(newCode)) {
                throw new IllegalArgumentException("Code '" + newCode + "' already exists");
            }
            location.setCode(newCode);
        }

        if ("CITY".equalsIgnoreCase(location.getType()) &&
                (req.getParentCode() != null || req.getParentId() != null)) {
            LocationMst parentState = resolveParentState(req);
            location.setParentId(parentState.getId());
            location.setParentCode(parentState.getCode());
        }

        return toResponse(locationMstRepository.save(location));
    }

    // ── Deactivate ───────────────────────────────────────────────────────────────

    @Transactional
    public void deactivate(Long id) {
        LocationMst location = locationMstRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found: " + id));
        location.setIsActive(false);
        locationMstRepository.save(location);
    }

    // ── Get States ───────────────────────────────────────────────────────────────

    public List<LocationResponse> getStates() {
        return locationMstRepository.findActiveStates()
                .stream().map(this::toResponse).toList();
    }

    // ── Get Cities by State ID ────────────────────────────────────────────────────

    public List<LocationResponse> getCitiesByState(Long stateId) {
        return locationMstRepository.findActiveCitiesByState(stateId)
                .stream().map(this::toResponse).toList();
    }

    // ── Get Cities by State Code ──────────────────────────────────────────────────

    public List<LocationResponse> getCitiesByStateCode(String stateCode) {
        String upper = stateCode.toUpperCase().trim();
        // Validate state exists
        locationMstRepository.findByCodeIgnoreCase(upper)
                .filter(s -> "STATE".equalsIgnoreCase(s.getType()))
                .orElseThrow(() -> new ResourceNotFoundException("State not found with code: " + upper));

        return locationMstRepository.findActiveCitiesByStateCode(upper)
                .stream().map(this::toResponse).toList();
    }

    // ── Get All ──────────────────────────────────────────────────────────────────

    public List<LocationResponse> getAll() {
        return locationMstRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    // ── States Map (for dropdown: code → name) ────────────────────────────────────

    public Map<String, String> getStatesMap() {
        Map<String, String> map = new LinkedHashMap<>();
        locationMstRepository.findActiveStates()
                .forEach(s -> map.put(s.getCode(), s.getName()));
        return map;
    }

    // ── Cities Map by State Code (for dropdown: code → name) ─────────────────────

    public Map<String, String> getCitiesMap(String stateCode) {
        String upper = stateCode.toUpperCase().trim();
        Map<String, String> map = new LinkedHashMap<>();
        locationMstRepository.findActiveCitiesByStateCode(upper)
                .forEach(c -> map.put(c.getCode(), c.getName()));
        return map;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

    private LocationMst resolveParentState(LocationRequest req) {
        if (req.getParentCode() != null && !req.getParentCode().isBlank()) {
            String stateCode = req.getParentCode().toUpperCase().trim();
            return locationMstRepository.findByCodeIgnoreCase(stateCode)
                    .filter(s -> "STATE".equalsIgnoreCase(s.getType()))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "State not found with code: " + stateCode));
        }
        if (req.getParentId() != null) {
            return locationMstRepository.findById(req.getParentId())
                    .filter(s -> "STATE".equalsIgnoreCase(s.getType()))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "State not found with id: " + req.getParentId()));
        }
        throw new IllegalArgumentException("parentCode (stateCode) or parentId is required for CITY");
    }

    private LocationResponse toResponse(LocationMst l) {
        String parentName = null;
        if ("CITY".equalsIgnoreCase(l.getType()) && l.getParentId() != null) {
            parentName = locationMstRepository.findById(l.getParentId())
                    .map(LocationMst::getName).orElse(null);
        }
        return LocationResponse.builder()
                .id(l.getId())
                .type(l.getType())
                .code(l.getCode())
                .name(l.getName())
                .parentId(l.getParentId())
                .parentCode(l.getParentCode())
                .parentName(parentName)
                .isActive(l.getIsActive())
                .build();
    }
}
