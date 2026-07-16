package com.stayease.booking_service.service;

import com.stayease.booking_service.dto.BookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventProducer {

    private static final String TOPIC = "booking-events";

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public void sendEvent(BookingEvent event) {

        kafkaTemplate.send(TOPIC, event);

        log.info("""
                =================================================
                Kafka Event Published
                Event Type : {}
                Booking Id : {}
                User       : {}
                Property   : {}
                Room       : {}
                Amount     : {}
                Time       : {}
                =================================================
                """,
                event.getEventType(),
                event.getBookingId(),
                event.getUserEmail(),
                event.getPropertyId(),
                event.getRoomId(),
                event.getAmount(),
                event.getEventTime());
    }
}