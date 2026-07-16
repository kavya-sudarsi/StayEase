package com.stayease.notification_service.service;

import com.stayease.notification_service.dto.BookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingCreated(BookingEvent event) {

        sendEmail(
                event.getUserEmail(),
                "StayEase - Booking Created",
                """
                Hello,

                Your booking has been created successfully.

                Booking ID : %d
                Property ID: %d
                Room ID    : %d
                Amount     : ₹%.2f

                Thank you for choosing StayEase.
                """
                        .formatted(
                                event.getBookingId(),
                                event.getPropertyId(),
                                event.getRoomId(),
                                event.getAmount()
                        )
        );
    }

    public void sendPaymentSuccess(BookingEvent event) {

        sendEmail(
                event.getUserEmail(),
                "StayEase - Payment Successful",
                """
                Hello,

                Your payment was successful.

                Booking ID : %d
                Payment ID : %s
                Amount     : ₹%.2f

                Your booking is now confirmed.

                Enjoy your stay!
                """
                        .formatted(
                                event.getBookingId(),
                                event.getPaymentId(),
                                event.getAmount()
                        )
        );
    }

    public void sendBookingCancelled(BookingEvent event) {

        sendEmail(
                event.getUserEmail(),
                "StayEase - Booking Cancelled",
                """
                Hello,

                Your booking has been cancelled.

                Booking ID : %d

                %s
                """
                        .formatted(
                                event.getBookingId(),
                                event.getMessage()
                        )
        );
    }

    public void sendBookingCompleted(BookingEvent event) {

        sendEmail(
                event.getUserEmail(),
                "StayEase - Thank You",
                """
                Hello,

                Your stay has been completed.

                Booking ID : %d

                Thank you for staying with StayEase.

                We hope to see you again.
                """
                        .formatted(
                                event.getBookingId()
                        )
        );
    }

    private void sendEmail(
            String to,
            String subject,
            String body
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        log.info("Email sent to {}", to);
    }
}