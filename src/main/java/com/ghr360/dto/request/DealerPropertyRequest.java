package com.ghr360.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DealerPropertyRequest {

    private String type;           // RENT / SALE
    private String ownerName;
    private String ownerContact;
    private String locality;
    private String address;
    private String city;
    private String state;
    private String dealer;
    private Double lat;
    private Double longitude;
    private BigDecimal price;
}
