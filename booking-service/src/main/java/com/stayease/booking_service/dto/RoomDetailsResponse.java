package com.stayease.booking_service.dto;

import lombok.Data;

@Data
public class RoomDetailsResponse {

    private Long id;

    private String roomNumber;

    private String roomType;

    private int totalBeds;

    private int availableBeds;

    private Double pricePerBed;

    private boolean wifi;

    private boolean ac;
}