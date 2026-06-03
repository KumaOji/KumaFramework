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

package com.kuma.boot.i18n.advice;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.i18n.annotation.I18nClass;
import com.kuma.boot.i18n.annotation.I18nField;
import com.kuma.boot.i18n.annotation.I18nIgnore;
import com.kuma.boot.i18n.config.I18nProperties;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 响应体国际化处理器.
 *
 * <p>对未标注 {@link I18nIgnore} 的接口，递归扫描响应对象中标注了 {@link I18nClass} 的类，
 * 将其 {@link I18nField} 字段值替换为当前 Locale 的翻译文本。
 *
 * @author kuma
 */
@RestControllerAdvice
public class I18nResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final Map<String, Expression> EXPR_CACHE = new HashMap<>();

    private final MessageSource messageSource;
    private final boolean useCodeAsDefaultMessage;
    private final Locale fallbackLocale;

    public I18nResponseAdvice(MessageSource messageSource, I18nProperties props) {
        this.messageSource = messageSource;
        this.useCodeAsDefaultMessage = props.isUseCodeAsDefaultMessage();

        String tag = props.getFallbackLanguageTag();
        if (tag != null && !tag.isBlank()) {
            String[] parts = tag.split("-");
            Assert.isTrue(parts.length == 2, "fallbackLanguageTag 格式错误，应为 zh-CN 形式");
            this.fallbackLocale = Locale.of(parts[0], parts[1]);
        } else {
            this.fallbackLocale = null;
        }
    }

    @Override
    public boolean supports(
            MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        AnnotatedElement element = returnType.getAnnotatedElement();
        return AnnotationUtils.findAnnotation(element, I18nIgnore.class) == null;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        try {
            translate(body);
        } catch (Exception ex) {
            LogUtils.error("[i18n] 响应体多语言处理异常", ex);
        }
        return body;
    }

    /** 递归翻译对象中被 {@link I18nClass} 标注的类的 {@link I18nField} 字段. */
    public void translate(Object source) {
        if (source == null) {
            return;
        }
        Class<?> clazz = source.getClass();
        if (clazz.getAnnotation(I18nClass.class) == null) {
            return;
        }
        for (Field field : ReflectUtil.getFields(clazz)) {
            Object value = ReflectUtil.getFieldValue(source, field);
            if (value instanceof String str) {
                I18nField i18nField = field.getAnnotation(I18nField.class);
                if (i18nField == null) {
                    continue;
                }
                if (!evalCondition(source, i18nField)) {
                    continue;
                }
                String code = resolveCode(source, str, i18nField);
                if (CharSequenceUtil.isEmpty(code)) {
                    continue;
                }
                Locale locale = LocaleContextHolder.getLocale();
                ReflectUtil.setFieldValue(source, field, codeToMessage(code, locale, str));
            } else if (value instanceof Collection<?> coll) {
                if (CollUtil.isEmpty(coll)) {
                    continue;
                }
                coll.forEach(this::translate);
            } else if (field.getType().isArray() && value != null) {
                for (Object element : (Object[]) value) {
                    translate(element);
                }
            } else {
                translate(value);
            }
        }
    }

    private boolean evalCondition(Object source, I18nField i18nField) {
        String expr = i18nField.condition();
        if (CharSequenceUtil.isEmpty(expr)) {
            return true;
        }
        Expression expression = EXPR_CACHE.computeIfAbsent(expr, PARSER::parseExpression);
        Boolean result = expression.getValue(source, Boolean.class);
        return result == null || result;
    }

    private String resolveCode(Object source, String fieldValue, I18nField i18nField) {
        String expr = i18nField.code();
        if (CharSequenceUtil.isEmpty(expr)) {
            return fieldValue;
        }
        Expression expression = EXPR_CACHE.computeIfAbsent(expr, PARSER::parseExpression);
        return expression.getValue(source, String.class);
    }

    private String codeToMessage(String code, Locale locale, String defaultMessage) {
        try {
            return messageSource.getMessage(code, null, locale);
        } catch (NoSuchMessageException e) {
            LogUtils.warn("[i18n] 未找到消息，code={}, locale={}", code, locale);
        }
        if (fallbackLocale != null && !fallbackLocale.equals(locale)) {
            try {
                return messageSource.getMessage(code, null, fallbackLocale);
            } catch (NoSuchMessageException e) {
                LogUtils.warn("[i18n] 回退语言也未找到消息，code={}, fallback={}", code, fallbackLocale);
            }
        }
        return useCodeAsDefaultMessage ? code : defaultMessage;
    }
}
