package com.stayease.booking_service.controller;

import com.stayease.booking_service.dto.BookingRequest;
import com.stayease.booking_service.dto.BookingResponse;
import com.stayease.booking_service.service.BookingService;
import com.stayease.booking_service.dto.OwnerBookingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // CREATE BOOKING
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response =
                bookingService.createBooking(userEmail, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // CANCEL BOOKING
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @RequestHeader("X-User-Email") String userEmail,
            @PathVariable Long bookingId) {

        BookingResponse response =
                bookingService.cancelBooking(userEmail, bookingId);

        return ResponseEntity.ok(response);
    }

    // GET MY BOOKINGS
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @RequestHeader("X-User-Email") String userEmail) {

        List<BookingResponse> bookings =
                bookingService.getMyBookings(userEmail);

        return ResponseEntity.ok(bookings);
    }
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<OwnerBookingResponse>>
    getBookingsByProperty(
            @PathVariable Long propertyId
    ) {

        return ResponseEntity.ok(
                bookingService.getBookingsByProperty(
                        propertyId
                )
        );
    }
}