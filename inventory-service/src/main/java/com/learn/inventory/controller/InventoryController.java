package com.learn.inventory.controller;

import com.learn.commons.inventory.InventoryRequestDto;
import com.learn.commons.inventory.InventoryResponseDto;
import com.learn.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/deduct")
    public InventoryResponseDto deduct(@RequestBody InventoryRequestDto inventoryRequestDto){
        return inventoryService.deduct(inventoryRequestDto);
    }

    @PostMapping("/add")
    public void add(@RequestBody InventoryRequestDto inventoryRequestDto){
        inventoryService.add(inventoryRequestDto);
    }
}
