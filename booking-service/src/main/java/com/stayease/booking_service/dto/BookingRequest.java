package com.stayease.booking_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {

    @NotNull
    private Long propertyId;

    @NotNull
    private Long roomId;
}