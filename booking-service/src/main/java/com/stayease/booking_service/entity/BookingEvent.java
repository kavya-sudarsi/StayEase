package com.stayease.booking_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingEvent {

    private Long bookingId;
    private String userEmail;
    private Long roomId;
    private String eventType;
}