package com.ghr360.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class DashboardResponseDto {
    private long totalProperties;
    private long rentCount;
    private long saleCount;
    private long totalDealers;
    private List<DealerPropertyResponse> recentProperties;
}