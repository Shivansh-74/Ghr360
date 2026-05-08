package com.ghr360.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "STATE_MST")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StateMst {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "IS_ACTIVE")
    @Builder.Default
    private Boolean isActive = true;
}
