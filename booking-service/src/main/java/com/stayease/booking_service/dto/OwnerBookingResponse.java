package com.stayease.booking_service.dto;

import com.stayease.booking_service.entity.BookingStatus;
import com.stayease.booking_service.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OwnerBookingResponse {

    private Long bookingId;
    private String userEmail;
    private Long propertyId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private Double amount;
    private String propertyName;
    private String roomNumber;
}