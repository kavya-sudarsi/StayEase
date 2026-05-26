package com.stayease.property_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomRequest {

    @NotBlank
    private String roomNumber;

    // 1-sharing / 2-sharing / 3-sharing
    private String roomType;

    @Min(1)
    private int totalBeds;

    @Min(1)
    private Double pricePerBed;

    private boolean wifi;

    private boolean ac;
}