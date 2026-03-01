/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.cloud.client.loadbalancer.LoadBalanced
 *  org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.web.reactive.function.client.ExchangeFilterFunction
 *  org.springframework.web.reactive.function.client.WebClient
 */
package com.kuma.cloud.bootstrap.loadbalancer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LoadBalancedConfiguration {
    private ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    @Autowired
    public void setReactorLoadBalancerExchangeFilterFunction(ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction) {
        this.reactorLoadBalancerExchangeFilterFunction = reactorLoadBalancerExchangeFilterFunction;
    }

    @Bean
    @LoadBalanced
    public WebClient webClient() {
        return WebClient.builder().filter((ExchangeFilterFunction)this.reactorLoadBalancerExchangeFilterFunction).build();
    }
}

