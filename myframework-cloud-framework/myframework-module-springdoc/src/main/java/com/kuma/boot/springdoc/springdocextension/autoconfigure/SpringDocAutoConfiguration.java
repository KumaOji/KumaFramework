/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.map.MapUtil
 *  io.swagger.v3.oas.models.Components
 *  io.swagger.v3.oas.models.OpenAPI
 *  io.swagger.v3.oas.models.security.SecurityRequirement
 *  io.swagger.v3.oas.models.security.SecurityScheme
 *  jakarta.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springdoc.core.configuration.SpringDocConfiguration
 *  org.springdoc.core.customizers.GlobalOpenApiCustomizer
 *  org.springdoc.core.customizers.OpenApiBuilderCustomizer
 *  org.springdoc.core.customizers.ServerBaseUrlCustomizer
 *  org.springdoc.core.properties.SpringDocConfigProperties
 *  org.springdoc.core.providers.JavadocProvider
 *  org.springdoc.core.service.OpenAPIService
 *  org.springdoc.core.service.SecurityService
 *  org.springdoc.core.utils.PropertyResolverUtils
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.http.CacheControl
 *  org.springframework.web.servlet.config.annotation.EnableWebMvc
 *  org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
 *  org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 */
package com.kuma.boot.springdoc.springdocextension.autoconfigure;

import cn.hutool.core.map.MapUtil;
import com.kuma.boot.springdoc.springdocextension.handler.BaseEnumParameterHandler;
import com.kuma.boot.springdoc.springdocextension.handler.OpenApiHandler;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@AutoConfiguration(before={SpringDocConfiguration.class})
@EnableConfigurationProperties(value={SpringDocExtensionProperties.class})
public class SpringDocAutoConfiguration
implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(SpringDocAutoConfiguration.class);

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[]{"/favicon.ico"}).addResourceLocations(new String[]{"classpath:/"});
        registry.addResourceHandler(new String[]{"/doc.html"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/"});
        registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"}).setCacheControl(CacheControl.maxAge((long)5L, (TimeUnit)TimeUnit.HOURS).cachePublic());
    }

    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer(SpringDocExtensionProperties properties) {
        return openApi -> {
            if (null != openApi.getPaths()) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    Components components = properties.getComponents();
                    if (null != components && MapUtil.isNotEmpty((Map)components.getSecuritySchemes())) {
                        Map securitySchemeMap = components.getSecuritySchemes();
                        pathItem.readOperations().forEach(operation -> {
                            SecurityRequirement securityRequirement = new SecurityRequirement();
                            List<String> list = securitySchemeMap.values().stream().map(SecurityScheme::getName).toList();
                            list.forEach(arg_0 -> ((SecurityRequirement)securityRequirement).addList(arg_0));
                            operation.addSecurityItem(securityRequirement);
                        });
                    }
                });
            }
        };
    }

    @Bean
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI, SecurityService securityParser, SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils, Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers, Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers, Optional<JavadocProvider> javadocProvider) {
        return new OpenApiHandler(openAPI, securityParser, springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomisers, serverBaseUrlCustomisers, javadocProvider);
    }

    @Bean
    public BaseEnumParameterHandler customParameterCustomizer() {
        return new BaseEnumParameterHandler();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[ContiNew Starter] - Auto Configuration 'ApiDoc' completed initialization.");
    }
}

