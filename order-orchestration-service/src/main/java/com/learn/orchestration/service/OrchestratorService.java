package com.learn.orchestration.service;

import com.learn.commons.orchestrator.WorkflowStepStatus;
import com.learn.commons.order.OrchestratorRequestDto;
import com.learn.commons.order.OrchestratorResponseDto;
import com.learn.commons.order.OrderStatus;
import com.learn.commons.payment.PaymentRequestDto;
import com.learn.commons.inventory.InventoryRequestDto;
import com.learn.orchestration.exceptions.WorkflowException;
import com.learn.orchestration.steps.InventoryStep;
import com.learn.orchestration.steps.PaymentStep;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrchestratorService {

    @Qualifier("payment")
    private final WebClient paymentClient;
    
    @Qualifier("inventory")
    private final WebClient inventoryClient;

    public OrchestratorService(WebClient paymentClient, WebClient inventoryClient) {
        this.paymentClient = paymentClient;
        this.inventoryClient = inventoryClient;
    }

    public Mono<OrchestratorResponseDto> orderProduct(final OrchestratorRequestDto requestDTO) {
        Workflow orderWorkflow = getOrderWorkflow(requestDTO);

        return Flux.fromStream(() -> orderWorkflow.getSteps().stream()).flatMap(WorkflowStep::process)
            .handle(((aBoolean, synchronousSink) -> {
                if (aBoolean) {
                    synchronousSink.next(true);
                } else {
                    synchronousSink.error(new WorkflowException("Order not processed."));
                }
            })).then(Mono.fromCallable(() -> getResponseDTO(requestDTO, OrderStatus.ORDER_COMPLETED)))
            .onErrorResume(ex -> revertOrder(orderWorkflow, requestDTO));

    }

    private Mono<OrchestratorResponseDto> revertOrder(final Workflow workflow, final OrchestratorRequestDto requestDTO) {
        return Flux.fromStream(() -> workflow.getSteps().stream())
            .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE))
            .flatMap(WorkflowStep::revert).retry(3)
            .then(Mono.just(getResponseDTO(requestDTO, OrderStatus.ORDER_CANCELLED)));
    }

    private Workflow getOrderWorkflow(OrchestratorRequestDto requestDTO) {
        WorkflowStep paymentStep = new PaymentStep(paymentClient, getPaymentRequestDto(requestDTO));
        WorkflowStep inventoryStep = new InventoryStep(inventoryClient, getInventoryRequestDto(requestDTO));
        return new OrderWorkflow(List.of(paymentStep, inventoryStep));
    }

    private OrchestratorResponseDto getResponseDTO(OrchestratorRequestDto requestDTO, OrderStatus status) {
        OrchestratorResponseDto responseDTO = new OrchestratorResponseDto();
        responseDTO.setOrderId(requestDTO.getOrderId());
        responseDTO.setAmount(requestDTO.getAmount());
        responseDTO.setProductId(requestDTO.getProductId());
        responseDTO.setUserId(requestDTO.getUserId());
        responseDTO.setStatus(status);
        return responseDTO;
    }

    private PaymentRequestDto getPaymentRequestDto(OrchestratorRequestDto requestDTO) {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setUserId(requestDTO.getUserId());
        paymentRequestDto.setAmount(requestDTO.getAmount());
        paymentRequestDto.setOrderId(requestDTO.getOrderId());
        return paymentRequestDto;
    }

    private InventoryRequestDto getInventoryRequestDto(OrchestratorRequestDto requestDTO) {
        InventoryRequestDto inventoryRequestDto = new InventoryRequestDto();
        inventoryRequestDto.setUserId(requestDTO.getUserId());
        inventoryRequestDto.setProductId(requestDTO.getProductId());
        inventoryRequestDto.setOrderId(requestDTO.getOrderId());
        return inventoryRequestDto;
    }
}
