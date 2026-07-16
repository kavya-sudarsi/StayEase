package com.stayease.booking_service.repository;

import com.stayease.booking_service.entity.Booking;
import com.stayease.booking_service.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmail(String userEmail);

    List<Booking> findByStatus(BookingStatus status);

    // CHECK OVERLAPPING BOOKINGS
    boolean existsByRoomIdAndStatusAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Long roomId,
            BookingStatus status,
            LocalDate checkOutDate,
            LocalDate checkInDate
    );

    List<Booking> findByPropertyId(Long propertyId);
}