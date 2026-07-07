package com.ghr360.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "DEALER_PROPERTIES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealerProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TYPE", length = 10)
    private String type;          // RENT / SALE

    @Column(name = "OWNER_NAME", length = 100)
    private String ownerName;

    @Column(name = "OWNER_CONTACT", length = 20)
    private String ownerContact;

    @Column(name = "LOCALITY", length = 150)
    private String locality;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "STATE", length = 100)
    private String state;

    @Column(name = "LAT")
    private Double lat;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name = "PRICE", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "THUMBNAIL_URL", length = 500)
    private String thumbnailUrl;

    @Column(name = "THUMBNAIL_PUBLIC_ID", length = 300)
    private String thumbnailPublicId;

    @Column(name = "SQUARE_FEET")
    private Double squareFeet;

    @Column(name = "BEDROOMS")
    private Double bedrooms;

    @Column(name = "BATHROOMS")
    private Double bathrooms;

    @Column(name = "PARKINGS")
    private Double parkings;

    @Column(name = "STATUS", length = 20, nullable = false)
    private String status = "OPEN";   // OPEN / SOLD / FULFILLED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEALER_CODE", referencedColumnName = "USERNAME")
    private User dealer;
}
