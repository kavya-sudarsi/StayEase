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

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public void sendEvent(BookingEvent event) {
        kafkaTemplate.send("booking-events", event);
        log.info("Kafka event sent: type={}, bookingId={}, roomId={}",
                event.getEventType(),
                event.getBookingId(),
                event.getRoomId());
    }
}