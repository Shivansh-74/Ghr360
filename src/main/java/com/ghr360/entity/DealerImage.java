package com.ghr360.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DEALER_IMAGES")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DealerImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROPERTY_ID", nullable = false)
    private DealerProperty property;

    @Column(name = "IMAGE_URL", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "UPLOADED_AT")
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
