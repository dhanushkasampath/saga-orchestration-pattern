package com.learn.commons.inventory;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class InventoryResponseDto {
    private Integer userId;
    private Integer productId;
    private UUID orderId;
    private InventoryStatus status;
}
