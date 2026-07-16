package com.stayease.booking_service.controller;

import com.stayease.booking_service.dto.payment.CreateOrderRequest;
import com.stayease.booking_service.dto.payment.CreateOrderResponse;
import com.stayease.booking_service.dto.payment.VerifyPaymentRequest;
import com.stayease.booking_service.dto.payment.VerifyPaymentResponse;
import com.stayease.booking_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) throws Exception {

        return paymentService.createOrder(
                request.getBookingId()
        );
    }

    @PostMapping("/verify")
    public VerifyPaymentResponse verifyPayment(@Valid @RequestBody VerifyPaymentRequest request) {

        return paymentService.verifyPayment(request);
    }
}