package com.stayease.booking_service.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.stayease.booking_service.client.PropertyClient;
import com.stayease.booking_service.dto.BookingEvent;
import com.stayease.booking_service.dto.payment.CreateOrderResponse;
import com.stayease.booking_service.dto.payment.VerifyPaymentRequest;
import com.stayease.booking_service.dto.payment.VerifyPaymentResponse;
import com.stayease.booking_service.entity.Booking;
import com.stayease.booking_service.entity.BookingStatus;
import com.stayease.booking_service.entity.PaymentStatus;
import com.stayease.booking_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final BookingRepository bookingRepository;
    private final PropertyClient propertyClient;
    private final BookingEventProducer bookingEventProducer;

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    public CreateOrderResponse createOrder(Long bookingId)
            throws Exception {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Booking not found"
                                ));

        if (booking.getAmount() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking amount not found"
            );
        }

        JSONObject options = new JSONObject();

        options.put(
                "amount",
                booking.getAmount().intValue() * 100
        );

        options.put(
                "currency",
                "INR"
        );

        options.put(
                "receipt",
                "booking_" + bookingId
        );

        Order order =
                razorpayClient.orders.create(options);

        return CreateOrderResponse.builder()
                .orderId(order.get("id"))
                .amount(booking.getAmount())
                .currency("INR")
                .razorpayKey(razorpayKey)
                .build();
    }

    public VerifyPaymentResponse verifyPayment(
            VerifyPaymentRequest request
    ) {

        try {

            JSONObject attributes =
                    new JSONObject();

            attributes.put(
                    "razorpay_order_id",
                    request.getRazorpayOrderId()
            );

            attributes.put(
                    "razorpay_payment_id",
                    request.getRazorpayPaymentId()
            );

            attributes.put(
                    "razorpay_signature",
                    request.getRazorpaySignature()
            );

            boolean valid =
                    Utils.verifyPaymentSignature(
                            attributes,
                            razorpaySecret
                    );

            if (!valid) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid payment signature"
                );
            }

            Booking booking =
                    bookingRepository.findById(
                                    request.getBookingId()
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Booking not found"
                                    ));

            if (booking.getPaymentStatus()
                    == PaymentStatus.SUCCESS) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Payment already completed"
                );
            }

            propertyClient.decreaseAvailableBeds(
                    booking.getRoomId()
            );

            booking.setPaymentId(
                    request.getRazorpayPaymentId()
            );

            booking.setPaymentStatus(
                    PaymentStatus.SUCCESS
            );

            booking.setStatus(
                    BookingStatus.CONFIRMED
            );

            Booking updatedBooking =
                    bookingRepository.save(booking);

            bookingEventProducer.sendEvent(

                    BookingEvent.builder()
                            .bookingId(updatedBooking.getId())
                            .userEmail(updatedBooking.getUserEmail())
                            .propertyId(updatedBooking.getPropertyId())
                            .roomId(updatedBooking.getRoomId())
                            .eventType("PAYMENT_SUCCESS")
                            .amount(updatedBooking.getAmount())
                            .paymentId(updatedBooking.getPaymentId())
                            .message("Payment completed successfully")
                            .eventTime(LocalDateTime.now())
                            .build()
            );

            return VerifyPaymentResponse.builder()
                    .message("Payment successful")
                    .paymentId(updatedBooking.getPaymentId())
                    .status("SUCCESS")
                    .build();

        } catch (Exception ex) {

            ex.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment verification failed"
            );
        }
    }
}