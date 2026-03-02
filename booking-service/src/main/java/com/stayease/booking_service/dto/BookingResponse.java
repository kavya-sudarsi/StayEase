package com.stayease.booking_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookingResponse {

    private Long id;
    private Long propertyId;
    private Long roomId;
    private LocalDate bookingDate;
    private String status;
}