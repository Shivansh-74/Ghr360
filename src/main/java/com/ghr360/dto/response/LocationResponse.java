package com.ghr360.dto.response;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String type;
    private String code;
    private String name;
    private Long parentId;
    private String parentCode;   // state code for cities
    private String parentName;   // state name for cities
    private Boolean isActive;
}
