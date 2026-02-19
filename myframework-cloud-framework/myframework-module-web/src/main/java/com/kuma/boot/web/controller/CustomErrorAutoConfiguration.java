/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.Servlet
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.boot.autoconfigure.condition.SearchStrategy
 *  org.springframework.boot.autoconfigure.web.WebProperties
 *  org.springframework.boot.web.server.autoconfigure.ServerProperties
 *  org.springframework.boot.webmvc.autoconfigure.error.BasicErrorController
 *  org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration
 *  org.springframework.boot.webmvc.error.DefaultErrorAttributes
 *  org.springframework.boot.webmvc.error.ErrorAttributes
 *  org.springframework.boot.webmvc.error.ErrorController
 *  org.springframework.context.annotation.Bean
 *  org.springframework.web.servlet.DispatcherServlet
 *  tools.jackson.databind.json.JsonMapper
 */
package com.kuma.boot.web.controller;

import jakarta.servlet.Servlet;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.boot.webmvc.autoconfigure.error.BasicErrorController;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;
import tools.jackson.databind.json.JsonMapper;

@AutoConfiguration(before={ErrorMvcAutoConfiguration.class})
@ConditionalOnClass(value={Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
public class CustomErrorAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(value={ErrorAttributes.class}, search=SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new CustomErrorAttributes();
    }

    @Bean
    @ConditionalOnMissingBean(value={ErrorController.class}, search=SearchStrategy.CURRENT)
    public BasicErrorController basicErrorController(JsonMapper jsonMapper, ErrorAttributes errorAttributes, ServerProperties serverProperties, WebProperties webProperties) {
        return new CustomErrorController(jsonMapper, errorAttributes, webProperties.getError());
    }
}

