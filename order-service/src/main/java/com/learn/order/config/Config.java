package com.learn.order.config;

import com.learn.commons.order.OrchestratorRequestDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class Config {

    @Bean
    public Sinks.Many<OrchestratorRequestDto> sink(){
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Flux<OrchestratorRequestDto> flux(Sinks.Many<OrchestratorRequestDto> sink){
        return sink.asFlux();
    }

}
