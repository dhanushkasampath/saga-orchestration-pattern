package com.learn.order.service;

import com.learn.commons.order.OrchestratorResponseDto;
import com.learn.order.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UpdateService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public UpdateService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public Mono<Void> updateOrder(OrchestratorResponseDto orchestratorResponseDto){
        return purchaseOrderRepository.findById(orchestratorResponseDto.getOrderId())
            .doOnNext(p -> p.setStatus(orchestratorResponseDto.getStatus()))
            .doOnNext(purchaseOrderRepository::save)
            .then();
    }
}
