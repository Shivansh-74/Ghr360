package com.ghr360.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerImageResponse {
    private Long id;
    private Long propertyId;
    private String imageUrl;
    private LocalDateTime uploadedAt;
}
