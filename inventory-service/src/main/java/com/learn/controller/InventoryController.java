package com.learn.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("inventory")
public class InventoryController {

    @PostMapping("/deduct")
    public void deduct(){

    }

    @PostMapping("/add")
    public void add(){

    }
}
