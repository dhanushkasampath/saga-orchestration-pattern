package com.learn.payment.controller;

import com.learn.commons.payment.PaymentRequestDto;
import com.learn.commons.payment.PaymentResponseDto;
import com.learn.payment.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/debit")
    public PaymentResponseDto debit(@RequestBody PaymentRequestDto paymentRequestDto){
        return paymentService.debit(paymentRequestDto);
    }

    @PostMapping("/credit")
    public void credit(@RequestBody PaymentRequestDto paymentRequestDto){
        paymentService.credit(paymentRequestDto);
    }
}
