package com.learn.orchestration.config;

import com.learn.commons.order.OrchestratorRequestDto;
import com.learn.commons.order.OrchestratorResponseDto;
import com.learn.orchestration.service.OrchestratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
public class OrchestratorConfig {
    private final OrchestratorService orchestratorService;

    public OrchestratorConfig(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @Bean
    public Function<Flux<OrchestratorRequestDto>, Flux<OrchestratorResponseDto>> processor() {
        return flux -> flux.flatMap(dto -> orchestratorService.orderProduct(dto))
                .doOnNext(dto -> System.out.println("Status : " + dto.getStatus()));
    }


}
