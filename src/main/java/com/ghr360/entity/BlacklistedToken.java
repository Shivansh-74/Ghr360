package com.ghr360.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "BLACKLISTED_TOKENS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TOKEN", nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(name = "BLACKLISTED_AT", nullable = false)
    private Instant blacklistedAt;

    @Column(name = "EXPIRES_AT", nullable = false)
    private Instant expiresAt;
}
