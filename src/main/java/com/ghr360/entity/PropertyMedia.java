package com.ghr360.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PROPERTY_MEDIA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PropertyMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PROPERTY_ID", nullable = false)
    private Long propertyId;

    @Column(name = "USER_CODE", nullable = false, length = 100)
    private String userCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEDIA_TYPE", nullable = false, length = 10)
    private MediaType mediaType;

    @Column(name = "URL", nullable = false, length = 600)
    private String url;

    @Column(name = "PUBLIC_ID", nullable = false, length = 300)
    private String publicId;

    @Builder.Default
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
