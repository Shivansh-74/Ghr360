package com.ghr360.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PACKAGE_TABLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PACKAGE_TYPE", length = 100)
    private String packageType;

    @Column(name = "DURATION")
    private Integer duration;

    @Column(name = "PACKAGE_START_DATE")
    private LocalDate packageStartDate;

    @Column(name = "PACKAGE_END_DATE")
    private LocalDate packageEndDate;

    @Column(name = "AMOUNT", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "PAYMENT_METHOD", length = 50)
    private String paymentMethod;

    @Column(name = "STATUS", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEALER_CODE", referencedColumnName = "USERNAME")
    private User dealer;
}
