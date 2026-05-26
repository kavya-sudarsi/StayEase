package com.stayease.booking_service.service;

import com.stayease.booking_service.client.PropertyClient;
import com.stayease.booking_service.dto.BookingRequest;
import com.stayease.booking_service.dto.BookingResponse;
import com.stayease.booking_service.entity.Booking;
import com.stayease.booking_service.entity.BookingEvent;
import com.stayease.booking_service.repository.BookingRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyClient propertyClient;
    private final BookingEventProducer bookingEventProducer;

    @CircuitBreaker(name = "propertyService", fallbackMethod = "bookingFallback")
    public BookingResponse createBooking(String userEmail,
                                         BookingRequest request) {

        try {
            propertyClient.decreaseAvailableBeds(request.getRoomId());

        } catch (FeignException.Conflict ex) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Booking failed: No beds available"
            );
        }

        Booking booking = Booking.builder()
                .userEmail(userEmail)
                .propertyId(request.getPropertyId())
                .roomId(request.getRoomId())
                .bookingDate(LocalDate.now())
                .status("CONFIRMED")
                .build();

        Booking saved = bookingRepository.save(booking);

        BookingEvent event = new BookingEvent(
                saved.getId(),
                userEmail,
                saved.getRoomId(),
                "BOOKING_CREATED"
        );

        bookingEventProducer.sendEvent(event);

        return mapToResponse(saved);
    }


    public BookingResponse bookingFallback(
            String userEmail,
            BookingRequest request,
            Exception ex) {

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Property service is currently unavailable. Please try again later."
        );
    }


    public BookingResponse cancelBooking(String userEmail, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Booking not found"));

        if (!booking.getUserEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can cancel only your own booking");
        }

        if (booking.getStatus().equals("CANCELLED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Booking already cancelled");
        }

        propertyClient.increaseAvailableBeds(booking.getRoomId());

        booking.setStatus("CANCELLED");

        Booking updated = bookingRepository.save(booking);

        BookingEvent event = new BookingEvent(
                updated.getId(),
                userEmail,
                updated.getRoomId(),
                "BOOKING_CANCELLED"
        );

        bookingEventProducer.sendEvent(event);

        return mapToResponse(updated);
    }

    public List<BookingResponse> getMyBookings(String userEmail) {

        return bookingRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .propertyId(booking.getPropertyId())
                .roomId(booking.getRoomId())
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .build();
    }
}