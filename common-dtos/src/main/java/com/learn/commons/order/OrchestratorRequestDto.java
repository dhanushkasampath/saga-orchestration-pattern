package com.learn.commons.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class OrchestratorRequestDto {
    private Integer userId;
    private Integer productId;
    private UUID orderId;
    private Double amount;
}
