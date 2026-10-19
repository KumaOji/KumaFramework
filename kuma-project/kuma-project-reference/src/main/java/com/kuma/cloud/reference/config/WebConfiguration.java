package com.kuma.cloud.reference.config;

import com.kuma.boot.web.autoconfigure.ActuatorHttpExchangeAutoConfiguration;
import com.kuma.boot.web.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({WebMvcAutoConfiguration.class, ActuatorHttpExchangeAutoConfiguration.class})
public class WebConfiguration {}
