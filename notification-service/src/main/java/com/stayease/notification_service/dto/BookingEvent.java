package com.stayease.notification_service.dto;

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