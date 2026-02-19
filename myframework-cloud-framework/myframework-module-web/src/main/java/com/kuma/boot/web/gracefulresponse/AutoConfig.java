/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.web.gracefulresponse;

import com.kuma.boot.web.gracefulresponse.advice.GlobalExceptionAdvice;
import com.kuma.boot.web.gracefulresponse.advice.NotVoidResponseBodyAdvice;
import com.kuma.boot.web.gracefulresponse.advice.ValidationExceptionAdvice;
import com.kuma.boot.web.gracefulresponse.advice.VoidResponseBodyAdvice;
import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.defaults.DefaultResponseFactory;
import com.kuma.boot.web.gracefulresponse.defaults.DefaultResponseStatusFactoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value={GracefulResponseProperties.class})
public class AutoConfig {
    @Bean
    @ConditionalOnMissingBean(value={GlobalExceptionAdvice.class})
    public GlobalExceptionAdvice globalExceptionAdvice() {
        return new GlobalExceptionAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(value={ValidationExceptionAdvice.class})
    public ValidationExceptionAdvice validationExceptionAdvice() {
        return new ValidationExceptionAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(value={NotVoidResponseBodyAdvice.class})
    public NotVoidResponseBodyAdvice notVoidResponseBodyAdvice() {
        return new NotVoidResponseBodyAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(value={VoidResponseBodyAdvice.class})
    public VoidResponseBodyAdvice voidResponseBodyAdvice() {
        return new VoidResponseBodyAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(value={ResponseFactory.class})
    public ResponseFactory responseBeanFactory() {
        return new DefaultResponseFactory();
    }

    @Bean
    @ConditionalOnMissingBean(value={ResponseStatusFactory.class})
    public ResponseStatusFactory responseStatusFactory() {
        return new DefaultResponseStatusFactoryImpl();
    }

    @Bean
    public ExceptionAliasRegister exceptionAliasRegister() {
        return new ExceptionAliasRegister();
    }

    @Bean
    public Init init() {
        return new Init();
    }
}

