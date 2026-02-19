/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.idev.excel.FastExcel
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.context.annotation.Bean
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandler
 *  org.springframework.web.reactive.config.WebFluxConfigurer
 *  org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
 *  org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
 *  org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 */
package com.kuma.boot.office.fastexcel;

import cn.idev.excel.FastExcel;
import com.kuma.boot.office.fastexcel.resolver.ExcelMethodArgumentResolver;
import com.kuma.boot.office.fastexcel.resolver.ExcelMethodReturnValueHandler;
import com.kuma.boot.office.fastexcel.resolver.ReactiveExcelMethodArgumentResolver;
import com.kuma.boot.office.fastexcel.resolver.ReactiveExcelMethodReturnValueHandler;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnClass(value={FastExcel.class})
public class ExcelAutoConfiguration {

    @AutoConfiguration
    @ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.REACTIVE)
    public static class ExcelWebFluxAutoConfiguration
    implements WebFluxConfigurer {
        @Bean
        public ReactiveExcelMethodReturnValueHandler reactiveExcelMethodReturnValueHandler() {
            return new ReactiveExcelMethodReturnValueHandler();
        }

        public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
            configurer.addCustomResolver(new org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver[]{new ReactiveExcelMethodArgumentResolver()});
        }
    }

    @AutoConfiguration
    @ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
    public static class ExcelWebMvcAutoConfiguration
    implements WebMvcConfigurer {
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new ExcelMethodArgumentResolver());
        }

        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
            handlers.add((HandlerMethodReturnValueHandler)new ExcelMethodReturnValueHandler());
        }
    }
}

