package com.learn.order.controller;

import com.learn.commons.order.OrderRequestDto;
import com.learn.commons.order.OrderResponseDto;
import com.learn.order.entity.PurchaseOrder;
import com.learn.order.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public Mono<PurchaseOrder> createOrder(@RequestBody Mono<OrderRequestDto> orderMono){
        return orderMono.flatMap(orderService::createOrder);
    }

    @PostMapping("/all")
    public Flux<OrderResponseDto> getOrders(){
        return orderService.getAllOrder();
    }
}
