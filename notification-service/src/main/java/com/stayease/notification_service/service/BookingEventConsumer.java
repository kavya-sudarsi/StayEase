package com.stayease.notification_service.service;

import com.stayease.notification_service.dto.BookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = "booking-events",
            groupId = "notification-group"
    )
    public void consume(BookingEvent event) {

        log.info("Received Event : {}", event.getEventType());

        switch (event.getEventType()) {

            case "BOOKING_CREATED":

                emailService.sendBookingCreated(event);

                log.info("Booking Created Event Processed");
                break;

            case "PAYMENT_SUCCESS":

                emailService.sendPaymentSuccess(event);

                log.info("Payment Success Event Processed");
                break;

            case "BOOKING_CANCELLED":

                emailService.sendBookingCancelled(event);

                log.info("Booking Cancelled Event Processed");
                break;

            case "BOOKING_COMPLETED":

                emailService.sendBookingCompleted(event);

                log.info("Booking Completed Event Processed");
                break;

            default:

                log.info("Unknown Event : {}", event);
        }
    }
}