package com.learn.orchestration.service;

import com.learn.commons.orchestrator.WorkflowStepStatus;
import reactor.core.publisher.Mono;

public interface WorkflowStep {
    WorkflowStepStatus getStatus();
    Mono<Boolean> process();
    Mono<Boolean> revert();
}
