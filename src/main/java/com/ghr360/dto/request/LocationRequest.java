package com.ghr360.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationRequest {

    @NotNull(message = "Type is required (CITY or STATE)")
    private String type;

    @NotBlank(message = "Name is required")
    private String name;

    // Required for STATE; optional for CITY (but must be unique, uppercase, max 5 chars)
    @Pattern(regexp = "^[A-Za-z0-9]{1,5}$", message = "Code must be alphanumeric, max 5 characters, no spaces")
    private String code;

    // For CITY: provide either parentId OR parentCode (stateCode)
    private Long parentId;
    private String parentCode;
}
