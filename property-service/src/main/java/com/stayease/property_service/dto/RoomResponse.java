package com.stayease.property_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {

    private Long id;

    private String roomNumber;

    private String roomType;

    private int totalBeds;

    private int availableBeds;

    private Double pricePerBed;

    private boolean wifi;

    private boolean ac;
}