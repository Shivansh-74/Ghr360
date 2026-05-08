package com.ghr360.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterResponse {
    private Long id;
    private String name;
    private Boolean isActive;
}
