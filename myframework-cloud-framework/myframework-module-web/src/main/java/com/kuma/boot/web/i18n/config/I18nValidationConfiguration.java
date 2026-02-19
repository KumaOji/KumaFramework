/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.MessageInterpolator
 *  jakarta.validation.Validator
 *  jakarta.validation.executable.ExecutableValidator
 *  org.hibernate.validator.spi.resourceloading.ResourceBundleLocator
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.AutoConfigureOrder
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnResource
 *  org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration
 *  org.springframework.context.MessageSource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Role
 *  org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
 *  org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator
 */
package com.kuma.boot.web.i18n.config;

import com.kuma.boot.web.i18n.EmptyCurlyToDefaultMessageInterpolator;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

@AutoConfigureOrder(value=-2147483648)
@AutoConfiguration(before={ValidationAutoConfiguration.class}, after={I18nMessageSourceAutoConfiguration.class})
@ConditionalOnClass(value={ExecutableValidator.class})
@ConditionalOnResource(resources={"classpath:META-INF/services/javax.validation.spi.ValidationProvider"})
public class I18nValidationConfiguration {
    @Bean
    @ConditionalOnBean(value={MessageSource.class})
    @ConditionalOnMissingBean(value={Validator.class, MessageInterpolator.class})
    public EmptyCurlyToDefaultMessageInterpolator messageInterpolator(MessageSource messageSource) {
        return new EmptyCurlyToDefaultMessageInterpolator((ResourceBundleLocator)new MessageSourceResourceBundleLocator(messageSource));
    }

    @Bean
    @Role(value=2)
    @ConditionalOnMissingBean(value={Validator.class})
    @ConditionalOnBean(value={MessageInterpolator.class})
    public static LocalValidatorFactoryBean defaultValidator(MessageInterpolator messageInterpolator, MessageSource messageSource) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setMessageInterpolator(messageInterpolator);
        factoryBean.setValidationMessageSource(messageSource);
        return factoryBean;
    }
}

