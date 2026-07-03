package com.ghr360.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerPropertyResponse {

    private Long id;
    private String type;
    private String ownerName;
    private String ownerContact;
    private String locality;
    private String address;
    private String city;
    private String state;
    private Double lat;
    private Double longitude;
    private BigDecimal price;
    private String dealerCode;   // username of the dealer
    private String thumbnailUrl; // Cloudinary thumbnail image URL
}
