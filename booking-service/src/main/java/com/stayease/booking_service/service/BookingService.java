package com.stayease.booking_service.service;

import com.stayease.booking_service.client.PropertyClient;
import com.stayease.booking_service.dto.BookingRequest;
import com.stayease.booking_service.dto.BookingResponse;
import com.stayease.booking_service.entity.Booking;
import com.stayease.booking_service.repository.BookingRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyClient propertyClient;

    public BookingResponse createBooking(String userEmail, BookingRequest request) {

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

        return mapToResponse(saved);
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

        return mapToResponse(updated);
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