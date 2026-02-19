/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.annotation.Bean
 *  org.springframework.web.service.registry.ImportHttpServices
 */
package com.kuma.boot.web.autoconfigure;

import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.service.registry.ImportHttpServices;

@AutoConfiguration
@ImportHttpServices(basePackages={"com.kuma.boot.api.*.inner"})
public class HttpExchangeAutoConfiguration {
    @Bean
    InMemoryHttpExchangeRepository httpExchangeRepository() {
        InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
        repository.setCapacity(20);
        repository.setReverse(true);
        return repository;
    }
}

