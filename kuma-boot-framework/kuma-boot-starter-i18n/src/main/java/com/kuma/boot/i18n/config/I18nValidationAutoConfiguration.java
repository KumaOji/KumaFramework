/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.i18n.config;

import com.kuma.boot.i18n.validator.EmptyCurlyToDefaultMessageInterpolator;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

/**
 * Bean Validation 国际化配置.
 *
 * <p>将 Spring {@link MessageSource} 接入 Hibernate Validator，使 {@code @NotEmpty}、
 * {@code @NotNull} 等注解的 message 支持 properties 文件翻译；
 * 同时启用 {@link EmptyCurlyToDefaultMessageInterpolator} 支持空花括号占位符语法。
 *
 * @author kuma
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfiguration(
        before = ValidationAutoConfiguration.class,
        after = I18nMessageSourceAutoConfiguration.class)
@ConditionalOnClass(ExecutableValidator.class)
public class I18nValidationAutoConfiguration {

    @Bean
    @ConditionalOnBean(MessageSource.class)
    @ConditionalOnMissingBean({Validator.class, MessageInterpolator.class})
    public EmptyCurlyToDefaultMessageInterpolator messageInterpolator(MessageSource messageSource) {
        return new EmptyCurlyToDefaultMessageInterpolator(
                new MessageSourceResourceBundleLocator(messageSource));
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(Validator.class)
    @ConditionalOnBean(MessageInterpolator.class)
    public static LocalValidatorFactoryBean defaultValidator(
            MessageInterpolator messageInterpolator, MessageSource messageSource) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setMessageInterpolator(messageInterpolator);
        factoryBean.setValidationMessageSource(messageSource);
        return factoryBean;
    }
}
