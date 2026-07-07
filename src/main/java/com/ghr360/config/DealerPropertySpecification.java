package com.ghr360.config;

import com.ghr360.dto.request.DealerPropertyFilterRequest;
import com.ghr360.entity.DealerProperty;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DealerPropertySpecification {

    /**
     * Builds a dynamic JPA Specification from filter criteria.
     * If username is null (admin mode), no dealer filter is applied.
     */
    public static Specification<DealerProperty> filterBy(
            String username,
            DealerPropertyFilterRequest filter) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ── DEALER filter: only when not admin ────────────────────────────
            if (username != null) {
                predicates.add(cb.equal(root.get("dealer").get("username"), username));
                // Non-admin (mobile/dealer) only sees OPEN properties
                predicates.add(cb.equal(cb.upper(root.get("status")), "OPEN"));
            }

            // ── OPTIONAL filters ─────────────────────────────────────────────

            if (filter.getType() != null && !filter.getType().isBlank()) {
                predicates.add(cb.equal(
                        cb.upper(root.get("type")),
                        filter.getType().toUpperCase()));
            }

            if (filter.getPropertyId() != null) {
                predicates.add(
                    cb.equal(root.get("id"), filter.getPropertyId())
                );
            }
            if (filter.getOwnerName() != null && !filter.getOwnerName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("ownerName")),
                        "%" + filter.getOwnerName().toLowerCase() + "%"));
            }

            if (filter.getCity() != null && !filter.getCity().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("city")),
                        "%" + filter.getCity().toLowerCase() + "%"));
            }

            if (filter.getState() != null && !filter.getState().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("state")),
                        "%" + filter.getState().toLowerCase() + "%"));
            }

            if (filter.getLocality() != null && !filter.getLocality().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("locality")),
                        "%" + filter.getLocality().toLowerCase() + "%"));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // Admin can filter by status explicitly
            if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                predicates.add(cb.equal(
                        cb.upper(root.get("status")),
                        filter.getStatus().toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
