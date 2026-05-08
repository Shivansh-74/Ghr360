package com.ghr360.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DealerPropertyFilterRequest {

    private String type;               // RENT / SALE (optional)
    private String ownerName;          // optional
    private String city;               // optional
    private String state;              // optional
    private String locality;           // optional
    private BigDecimal minPrice;       // optional
    private BigDecimal maxPrice;       // optional
    private LocalDate startDate;       // optional — created date range (agar audit chahiye)
    private LocalDate endDate;         // optional
    private String isAdmin;
    private Long propertyId;
}
