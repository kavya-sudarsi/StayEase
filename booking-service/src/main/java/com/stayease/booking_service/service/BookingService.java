package com.stayease.booking_service.service;

import com.stayease.booking_service.client.PropertyClient;
import com.stayease.booking_service.dto.BookingEvent;
import com.stayease.booking_service.dto.BookingRequest;
import com.stayease.booking_service.dto.BookingResponse;
import com.stayease.booking_service.entity.Booking;
import com.stayease.booking_service.entity.BookingStatus;
import com.stayease.booking_service.repository.BookingRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyClient propertyClient;
    private final BookingEventProducer bookingEventProducer;

    // CREATE BOOKING
    @CircuitBreaker(
            name = "propertyService",
            fallbackMethod = "bookingFallback"
    )
    public BookingResponse createBooking(
            String userEmail,
            BookingRequest request
    ) {
        // DATE VALIDATION
        if (request.getCheckOutDate()
                .isBefore(request.getCheckInDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-out date cannot be before check-in date"
            );
        }

        // OVERLAP VALIDATION
        boolean alreadyBooked = bookingRepository
                .existsByRoomIdAndStatusAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                        request.getRoomId(),
                        BookingStatus.CONFIRMED,
                        request.getCheckOutDate(),
                        request.getCheckInDate()
                );

        if (alreadyBooked) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room already booked for selected dates"
            );
        }

        // DECREASE BEDS
        try {
            propertyClient.decreaseAvailableBeds(request.getRoomId());
        } catch (FeignException.Conflict ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No beds available"
            );
        }

        Booking booking = Booking.builder()
                .userEmail(userEmail)
                .propertyId(request.getPropertyId())
                .roomId(request.getRoomId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);

        // SEND KAFKA EVENT
        bookingEventProducer.sendEvent(new BookingEvent(
                saved.getId(),
                userEmail,
                saved.getRoomId(),
                "BOOKING_CREATED"
        ));

        return mapToResponse(saved);
    }

    // FALLBACK
    public BookingResponse bookingFallback(
            String userEmail,
            BookingRequest request,
            Exception ex
    ) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Property service unavailable. Please try again later."
        );
    }

    // CANCEL BOOKING
    @CircuitBreaker(
            name = "propertyService",
            fallbackMethod = "cancelFallback"
    )
    public BookingResponse cancelBooking(
            String userEmail,
            Long bookingId
    ) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));

        if (!booking.getUserEmail().equals(userEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can cancel only your booking"
            );
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking already cancelled"
            );
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed booking cannot be cancelled"
            );
        }

        // INCREASE BEDS
        propertyClient.increaseAvailableBeds(booking.getRoomId());

        booking.setStatus(BookingStatus.CANCELLED);

        Booking updated = bookingRepository.save(booking);

        // SEND KAFKA EVENT
        bookingEventProducer.sendEvent(new BookingEvent(
                updated.getId(),
                userEmail,
                updated.getRoomId(),
                "BOOKING_CANCELLED"
        ));

        return mapToResponse(updated);
    }

    // CANCEL FALLBACK
    public BookingResponse cancelFallback(
            String userEmail,
            Long bookingId,
            Exception ex
    ) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Property service unavailable. Please try again later."
        );
    }

    // GET MY BOOKINGS
    public List<BookingResponse> getMyBookings(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // SCHEDULED — MARK COMPLETED BOOKINGS (runs every day at midnight)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateCompletedBookings() {

        List<Booking> confirmed = bookingRepository
                .findByStatus(BookingStatus.CONFIRMED);

        for (Booking booking : confirmed) {
            if (booking.getCheckOutDate()
                    .isBefore(LocalDate.now())) {
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepository.save(booking);
                log.info("Booking {} marked as COMPLETED",
                        booking.getId());
            }
        }
    }

    // RESPONSE MAPPING
    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userEmail(booking.getUserEmail())
                .propertyId(booking.getPropertyId())
                .roomId(booking.getRoomId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .status(booking.getStatus())
                .build();
    }
}