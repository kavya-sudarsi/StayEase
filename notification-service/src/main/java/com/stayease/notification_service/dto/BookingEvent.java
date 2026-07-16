package com.stayease.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {

    private Long bookingId;

    private String userEmail;

    private Long propertyId;

    private Long roomId;

    private String eventType;

    private Double amount;

    private String paymentId;

    private String message;

    private LocalDateTime eventTime;
}