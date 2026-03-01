package com.kuma.cloud.bootstrap.function;

import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class CustomFunctions {

    @Bean
    public Function<String, String> toUpperCase() {
        return value -> value.toUpperCase();
    }

    @Bean
    public Function<String, String> toLowerCase() {
        return value -> value.toLowerCase();
    }

    @Bean
    public Function<Flux<String>, Flux<String>> uppercaseTest() {
        return flux -> flux.map(value -> value.toUpperCase());
    }

    @Bean
    public Function<Flux<String>, Flux<String>> lowercaseTest() {
        return flux -> flux.map(value -> value.toLowerCase());
    }
}
