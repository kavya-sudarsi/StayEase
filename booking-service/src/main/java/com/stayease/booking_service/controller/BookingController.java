package com.stayease.booking_service.controller;

import com.stayease.booking_service.dto.BookingRequest;
import com.stayease.booking_service.dto.BookingResponse;
import com.stayease.booking_service.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody BookingRequest request) {

        return bookingService.createBooking(userEmail, request);
    }

    @PutMapping("/{bookingId}/cancel")
    public BookingResponse cancelBooking(
            @RequestHeader("X-User-Email") String userEmail,
            @PathVariable Long bookingId) {

        return bookingService.cancelBooking(userEmail, bookingId);
    }
}