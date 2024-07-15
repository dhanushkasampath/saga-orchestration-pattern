package com.learn.inventory.service;

import com.learn.commons.inventory.InventoryRequestDto;
import com.learn.commons.inventory.InventoryResponseDto;
import com.learn.commons.inventory.InventoryStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryService {

    private Map<Integer, Integer> inventoryMap;

    @PostConstruct
    private void init(){
        inventoryMap = new HashMap<>();
        // key- productId | value-available quantity of that product
        inventoryMap.put(1, 2);
        inventoryMap.put(2, 3);
        inventoryMap.put(3, 4);
    }

    public InventoryResponseDto deduct(InventoryRequestDto inventoryRequestDto){
        int qty = inventoryMap.getOrDefault(inventoryRequestDto.getProductId(), 0);// 1st param is the key of the map

        InventoryResponseDto inventoryResponseDto = new InventoryResponseDto();
        inventoryResponseDto.setOrderId(inventoryRequestDto.getOrderId());
        inventoryResponseDto.setProductId(inventoryRequestDto.getProductId());
        inventoryResponseDto.setUserId(inventoryRequestDto.getUserId());
        inventoryResponseDto.setStatus(InventoryStatus.UNAVAILABLE);

        if (qty > 0) {
            inventoryResponseDto.setStatus(InventoryStatus.AVAILABLE);
            inventoryMap.put(-inventoryRequestDto.getProductId(), qty - 1);
        }

        return inventoryResponseDto;
    }

    public void add(InventoryRequestDto inventoryRequestDto) {
        inventoryMap.computeIfPresent(inventoryRequestDto.getProductId(), (k, v) -> v +1);
    }
}
