package com.learn.commons.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class PaymentRequestDto {
    private Integer userId;
    private UUID orderId;
    private Double amount;
}
