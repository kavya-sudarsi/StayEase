package com.stayease.booking_service.service;

import com.stayease.booking_service.client.PropertyClient;
import com.stayease.booking_service.dto.BookingEvent;
import com.stayease.booking_service.dto.BookingRequest;
import com.stayease.booking_service.dto.BookingResponse;
import com.stayease.booking_service.dto.RoomDetailsResponse;
import com.stayease.booking_service.entity.Booking;
import com.stayease.booking_service.entity.BookingStatus;
import com.stayease.booking_service.entity.PaymentStatus;
import com.stayease.booking_service.repository.BookingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.stayease.booking_service.dto.OwnerBookingResponse;
import com.stayease.booking_service.dto.PropertyDetailsResponse;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyClient propertyClient;
    private final BookingEventProducer bookingEventProducer;

    @CircuitBreaker(
            name = "propertyService",
            fallbackMethod = "bookingFallback"
    )
    public BookingResponse createBooking(
            String userEmail,
            BookingRequest request
    ) {

        if (request.getCheckOutDate()
                .isBefore(request.getCheckInDate())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-out date cannot be before check-in date"
            );
        }

        boolean alreadyBooked =
                bookingRepository
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

        RoomDetailsResponse room =
                propertyClient.getRoom(
                        request.getRoomId()
                );

        Booking booking = Booking.builder()
                .userEmail(userEmail)
                .propertyId(request.getPropertyId())
                .roomId(request.getRoomId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .status(BookingStatus.PENDING_PAYMENT)
                .amount(room.getPricePerBed())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Booking saved =
                bookingRepository.save(booking);

//        bookingEventProducer.sendEvent(
//
//                BookingEvent.builder()
//                        .bookingId(saved.getId())
//                        .userEmail(saved.getUserEmail())
//                        .propertyId(saved.getPropertyId())
//                        .roomId(saved.getRoomId())
//                        .eventType("BOOKING_CREATED")
//                        .amount(saved.getAmount())
//                        .paymentId(saved.getPaymentId())
//                        .message("Booking created successfully")
//                        .eventTime(java.time.LocalDateTime.now())
//                        .build()
//        );

        return mapToResponse(saved);
    }

    public BookingResponse bookingFallback(
            String userEmail,
            BookingRequest request,
            Exception ex) {

        ex.printStackTrace();
        log.error("Fallback triggered", ex);

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getClass().getName() + " : " + ex.getMessage()
        );
    }

    @CircuitBreaker(
            name = "propertyService",
            fallbackMethod = "cancelFallback"
    )
    public BookingResponse cancelBooking(
            String userEmail,
            Long bookingId
    ) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
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

        if (booking.getPaymentStatus()
                == PaymentStatus.SUCCESS) {

            propertyClient.increaseAvailableBeds(
                    booking.getRoomId()
            );
        }

        booking.setStatus(
                BookingStatus.CANCELLED
        );

        Booking updated =
                bookingRepository.save(booking);

//        bookingEventProducer.sendEvent(
//
//                BookingEvent.builder()
//                        .bookingId(updated.getId())
//                        .userEmail(updated.getUserEmail())
//                        .propertyId(updated.getPropertyId())
//                        .roomId(updated.getRoomId())
//                        .eventType("BOOKING_CANCELLED")
//                        .amount(updated.getAmount())
//                        .paymentId(updated.getPaymentId())
//                        .message("Booking cancelled")
//                        .eventTime(java.time.LocalDateTime.now())
//                        .build()
//        );

        return mapToResponse(updated);
    }

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

    public List<BookingResponse> getMyBookings(
            String userEmail
    ) {

        return bookingRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<OwnerBookingResponse> getBookingsByProperty(
            Long propertyId
    ) {

        return bookingRepository.findByPropertyId(propertyId)
                .stream().map(booking -> {

                    PropertyDetailsResponse property =
                            propertyClient.getProperty(
                                    booking.getPropertyId()
                            );

                    RoomDetailsResponse room =
                            propertyClient.getRoom(
                                    booking.getRoomId()
                            );

                    return OwnerBookingResponse.builder()

                            .bookingId(booking.getId())

                            .userEmail(booking.getUserEmail())

                            .propertyId(booking.getPropertyId())

                            .roomId(booking.getRoomId())

                            .propertyName(property.getName())

                            .roomNumber(room.getRoomNumber())

                            .checkInDate(booking.getCheckInDate())

                            .checkOutDate(booking.getCheckOutDate())

                            .status(booking.getStatus())

                            .paymentStatus(booking.getPaymentStatus())

                            .amount(booking.getAmount())

                            .build();

                })
                .toList();
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateCompletedBookings() {

        List<Booking> confirmed =
                bookingRepository.findByStatus(
                        BookingStatus.CONFIRMED
                );

        for (Booking booking : confirmed) {

            if (booking.getCheckOutDate()
                    .isBefore(LocalDate.now())) {

                booking.setStatus(
                        BookingStatus.COMPLETED
                );

                bookingRepository.save(
                        booking
                );

//                bookingEventProducer.sendEvent(
//
//                        BookingEvent.builder()
//                                .bookingId(booking.getId())
//                                .userEmail(booking.getUserEmail())
//                                .propertyId(booking.getPropertyId())
//                                .roomId(booking.getRoomId())
//                                .eventType("BOOKING_COMPLETED")
//                                .amount(booking.getAmount())
//                                .paymentId(booking.getPaymentId())
//                                .message("Stay completed")
//                                .eventTime(java.time.LocalDateTime.now())
//                                .build()
//                );

                log.info(
                        "Booking {} marked as COMPLETED",
                        booking.getId()
                );
            }
        }
    }

    private BookingResponse mapToResponse(
            Booking booking
    ) {

        PropertyDetailsResponse property =
                propertyClient.getProperty(
                        booking.getPropertyId()
                );

        RoomDetailsResponse room =
                propertyClient.getRoom(
                        booking.getRoomId()
                );

        return BookingResponse.builder()

                .id(booking.getId())

                .userEmail(booking.getUserEmail())

                .propertyId(booking.getPropertyId())

                .roomId(booking.getRoomId())

                .propertyName(property.getName())

                .city(property.getCity())

                .state(property.getState())

                .imageUrl(property.getImageUrl())

                .roomNumber(room.getRoomNumber())

                .roomType(room.getRoomType())

                .checkInDate(booking.getCheckInDate())

                .checkOutDate(booking.getCheckOutDate())

                .status(booking.getStatus())

                .amount(booking.getAmount())

                .paymentStatus(booking.getPaymentStatus())

                .paymentId(booking.getPaymentId())

                .build();
    }
}