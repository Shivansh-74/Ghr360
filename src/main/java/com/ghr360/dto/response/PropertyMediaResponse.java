package com.ghr360.dto.response;

import com.ghr360.entity.MediaType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMediaResponse {

    private Long id;
    private Long propertyId;
    private String userCode;
    private MediaType mediaType;
    private String url;
    private String publicId;
    private LocalDateTime createdAt;
}
