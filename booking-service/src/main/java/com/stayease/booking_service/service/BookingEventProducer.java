package com.stayease.booking_service.service;

import com.stayease.booking_service.entity.BookingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingEventProducer {

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public void sendEvent(BookingEvent event) {
        kafkaTemplate.send("booking-events", event);
        System.out.println("Event Sent to Kafka: " + event);
    }
}