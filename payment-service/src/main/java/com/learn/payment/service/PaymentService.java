package com.learn.payment.service;

import com.learn.commons.inventory.InventoryRequestDto;
import com.learn.commons.inventory.InventoryResponseDto;
import com.learn.commons.inventory.InventoryStatus;
import com.learn.commons.payment.PaymentRequestDto;
import com.learn.commons.payment.PaymentResponseDto;
import com.learn.commons.payment.PaymentStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private Map<Integer, Double> paymentMap;

    @PostConstruct
    private void init(){
        paymentMap = new HashMap<>();
        // key- userId value-amount of money that particular user has
        paymentMap.put(1, 500d);
        paymentMap.put(2, 1000d);
        paymentMap.put(3, 700d);
    }

    public PaymentResponseDto debit(PaymentRequestDto paymentRequestDto){
        double balance = paymentMap.getOrDefault(paymentRequestDto.getUserId(), 0d);// 1st param is the key of the map

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setOrderId(paymentRequestDto.getOrderId());
        paymentResponseDto.setUserId(paymentRequestDto.getUserId());
        paymentResponseDto.setAmount(paymentRequestDto.getAmount());
        paymentResponseDto.setStatus(PaymentStatus.PAYMENT_REJECTED);

        if (balance >= paymentRequestDto.getAmount()) {
            paymentResponseDto.setStatus(PaymentStatus.PAYMENT_APPROVED);
            paymentMap.put(paymentRequestDto.getUserId(), balance - paymentRequestDto.getAmount());
        }

        return paymentResponseDto;
    }

    public void credit(PaymentRequestDto paymentRequestDto) {
        paymentMap.computeIfPresent(paymentRequestDto.getUserId(), (k, v) -> v + paymentRequestDto.getAmount());
    }
}
