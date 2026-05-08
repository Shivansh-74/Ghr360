package com.ghr360.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "location_mst",
    uniqueConstraints = @UniqueConstraint(name = "uk_location_code", columnNames = "CODE")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LocationMst {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TYPE", nullable = false, length = 10)
    private String type;

    // Unique code e.g. MP, MH for states; BPL, IND for cities
    @Column(name = "CODE", nullable = false, length = 10, unique = true)
    private String code;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    // null for STATE; for CITY: references parent STATE's id
    @Column(name = "PARENT_ID")
    private Long parentId;

    // Denormalized parent state code for easy code-based lookups
    @Column(name = "PARENT_CODE", length = 10)
    private String parentCode;

    @Column(name = "IS_ACTIVE")
    @Builder.Default
    private Boolean isActive = true;
}
