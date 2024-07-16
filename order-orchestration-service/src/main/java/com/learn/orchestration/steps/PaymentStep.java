package com.learn.orchestration.steps;

import com.learn.commons.orchestrator.WorkflowStepStatus;
import com.learn.commons.payment.PaymentResponseDto;
import com.learn.commons.payment.PaymentStatus;
import com.learn.commons.payment.PaymentRequestDto;
import com.learn.orchestration.service.WorkflowStep;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class PaymentStep implements WorkflowStep {

    private final WebClient webClient;
    private final PaymentRequestDto requestDTO;
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;

    public PaymentStep(WebClient webClient, PaymentRequestDto requestDTO) {
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
                .uri("/payment/debit")
                .body(BodyInserters.fromValue(requestDTO))
                .retrieve()
                .bodyToMono(PaymentResponseDto.class)
                .map(r -> r.getStatus().equals(PaymentStatus.PAYMENT_APPROVED))
                .doOnNext(b -> stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED);
    }

    @Override
    public Mono<Boolean> revert() {
        return this.webClient
                .post()
                .uri("/payment/credit")
                .body(BodyInserters.fromValue(requestDTO))
                .retrieve()
                .bodyToMono(Void.class)
                .map(r -> true)
                .onErrorReturn(false);
    }

}
