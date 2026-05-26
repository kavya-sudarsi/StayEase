package com.stayease.notification_service.service;

import com.stayease.notification_service.dto.BookingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(topics = "booking-events", groupId = "notification-group")
    public void consume(BookingEvent event) {

        System.out.println("\n========== BOOKING EVENT RECEIVED ==========");

        System.out.println("Booking ID : " + event.getBookingId());
        System.out.println("User Email : " + event.getUserEmail());
        System.out.println("Room ID    : " + event.getRoomId());
        System.out.println("Event Type : " + event.getEventType());

        // Simulate email sending
        System.out.println(
                " Email sent to " + event.getUserEmail()
        );

        System.out.println("===========================================\n");
    }
}