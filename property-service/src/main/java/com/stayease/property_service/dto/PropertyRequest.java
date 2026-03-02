package com.stayease.property_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PropertyRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;
}