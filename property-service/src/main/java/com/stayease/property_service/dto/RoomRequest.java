package com.stayease.property_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomRequest {

    @NotBlank
    private String roomNumber;

    @Min(1)
    private int totalBeds;
}