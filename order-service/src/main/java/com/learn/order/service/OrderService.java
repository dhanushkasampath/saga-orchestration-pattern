package com.learn.order.service;

import com.learn.commons.order.OrchestratorRequestDto;
import com.learn.commons.order.OrderRequestDto;
import com.learn.commons.order.OrderResponseDto;
import com.learn.commons.order.OrderStatus;
import com.learn.order.entity.PurchaseOrder;
import com.learn.order.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;

@Service
public class OrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final Sinks.Many<OrchestratorRequestDto> sink;

    public OrderService(PurchaseOrderRepository purchaseOrderRepository, Sinks.Many<OrchestratorRequestDto> sink) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.sink = sink;
    }

    private static final Map<Integer, Double> ORDER_PRICE = Map.of(
            1, 100d,
            2, 200d,
            3, 300d
    );

    public Mono<PurchaseOrder> createOrder(OrderRequestDto orderRequestDto){
        return purchaseOrderRepository.save(dtoToEntity(orderRequestDto)) // persist the data in db of order ms
            .doOnNext(e -> orderRequestDto.setOrderId(e.getId()))
            .doOnNext(e -> emitEvent(orderRequestDto)); // pushing the event to kafka
    }

    public Flux<OrderResponseDto> getAllOrder(){
        return purchaseOrderRepository.findAll()
            .map(this::entityToDto);
    }



    private void emitEvent(OrderRequestDto orderRequestDto){
        sink.tryEmitNext(getOrchestratorRequestDto(orderRequestDto));
    }

    private OrchestratorRequestDto getOrchestratorRequestDto(OrderRequestDto orderRequestDto) {
        OrchestratorRequestDto orchestratorRequestDto = new OrchestratorRequestDto();
        orchestratorRequestDto.setUserId(orderRequestDto.getUserId());
        orchestratorRequestDto.setAmount(ORDER_PRICE.get(orderRequestDto.getProductId()));
        orchestratorRequestDto.setOrderId(orderRequestDto.getOrderId());
        orchestratorRequestDto.setProductId(orderRequestDto.getProductId());
        return orchestratorRequestDto;
    }

    private PurchaseOrder dtoToEntity(OrderRequestDto orderRequestDto) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(orderRequestDto.getOrderId());
        purchaseOrder.setProductId(orderRequestDto.getProductId());
        purchaseOrder.setUserId(orderRequestDto.getUserId());
        purchaseOrder.setPrice(ORDER_PRICE.get(purchaseOrder.getProductId()));
        purchaseOrder.setStatus(OrderStatus.ORDER_CREATED);
        return purchaseOrder;
    }

  private OrderResponseDto entityToDto(PurchaseOrder purchaseOrder) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderId(purchaseOrder.getId());
        orderResponseDto.setProductId(purchaseOrder.getProductId());
        orderResponseDto.setUserId(purchaseOrder.getUserId());
        orderResponseDto.setAmount(purchaseOrder.getPrice());
        orderResponseDto.setStatus(purchaseOrder.getStatus());
        return orderResponseDto;
  }
}
