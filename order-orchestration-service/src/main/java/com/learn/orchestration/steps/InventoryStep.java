package com.learn.orchestration.steps;

import com.learn.commons.inventory.InventoryRequestDto;
import com.learn.commons.inventory.InventoryResponseDto;
import com.learn.commons.inventory.InventoryStatus;
import com.learn.commons.orchestrator.WorkflowStepStatus;
import com.learn.orchestration.service.WorkflowStep;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class InventoryStep implements WorkflowStep {

    private final WebClient webClient;
    private final InventoryRequestDto requestDTO;
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;

    public InventoryStep(WebClient webClient, InventoryRequestDto requestDTO) {
        this.webClient = webClient;
        this.requestDTO = requestDTO;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return stepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        return webClient
            .post()
            .uri("/inventory/deduct")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(InventoryResponseDto.class)
            .map(r -> r.getStatus().equals(InventoryStatus.AVAILABLE))
            .doOnNext(b -> stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED);
    }

    @Override
    public Mono<Boolean> revert() {
        return webClient
            .post()
            .uri("/inventory/add")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(Void.class)
            .map(r -> true)
            .onErrorReturn(false);
    }
}