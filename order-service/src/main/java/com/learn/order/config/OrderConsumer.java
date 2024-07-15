package com.learn.order.config;

import com.learn.commons.order.OrchestratorRequestDto;
import com.learn.commons.order.OrchestratorResponseDto;
import com.learn.order.service.UpdateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
public class OrderConsumer {

    private final Flux<OrchestratorRequestDto> flux;
    private final UpdateService updateService;

    public OrderConsumer(Flux<OrchestratorRequestDto> flux, UpdateService updateService) {
        this.flux = flux;
        this.updateService = updateService;
    }

    @Bean
    public Supplier<Flux<OrchestratorRequestDto>> supplier(){
        return () -> flux;
    }

    public Consumer<Flux<OrchestratorResponseDto>> consumer() {
        return c -> c.doOnNext(a -> System.out.println("Consuming::" + a))
            .flatMap(orchestratorResponseDto -> updateService.updateOrder(orchestratorResponseDto))
            .subscribe();
    }
}
