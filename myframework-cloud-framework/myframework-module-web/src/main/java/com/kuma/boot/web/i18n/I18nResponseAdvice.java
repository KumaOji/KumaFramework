/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.lang.Assert
 *  cn.hutool.core.text.CharSequenceUtil
 *  cn.hutool.core.util.ReflectUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.context.MessageSource
 *  org.springframework.context.NoSuchMessageException
 *  org.springframework.context.i18n.LocaleContextHolder
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 *  org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
 */
package com.kuma.boot.web.i18n;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.utils.log.LogUtils;

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

@RestControllerAdvice
public class I18nResponseAdvice
implements ResponseBodyAdvice<Object> {
    private final MessageSource messageSource;
    private final boolean useCodeAsDefaultMessage;
    private Locale fallbackLocale = null;
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final Map<String, Expression> EXPRESSION_CACHE = new HashMap<String, Expression>();

    public I18nResponseAdvice(MessageSource messageSource, I18nOptions i18nOptions) {
        this.messageSource = messageSource;
        String fallbackLanguageTag = i18nOptions.getFallbackLanguageTag();
        if (fallbackLanguageTag != null) {
            String[] arr = fallbackLanguageTag.split("-");
            Assert.isTrue((arr.length == 2 ? 1 : 0) != 0, (String)"error fallbackLanguageTag!", (Object[])new Object[0]);
            this.fallbackLocale = Locale.of(arr[0], arr[1]);
        }
        this.useCodeAsDefaultMessage = i18nOptions.isUseCodeAsDefaultMessage();
    }

    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        AnnotatedElement annotatedElement = returnType.getAnnotatedElement();
        I18nIgnore i18nIgnore = (I18nIgnore)AnnotationUtils.findAnnotation((AnnotatedElement)annotatedElement, I18nIgnore.class);
        return i18nIgnore == null;
    }

    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            this.switchLanguage(body);
        }
        catch (Exception ex) {
            LogUtils.error((String)"[\u56fd\u9645\u5316]\u54cd\u5e94\u4f53\u56fd\u9645\u5316\u5904\u7406\u5f02\u5e38\uff1a{}", (Object[])new Object[]{body});
        }
        return body;
    }

    public void switchLanguage(Object source) {
        if (source == null) {
            return;
        }
        Class<?> sourceClass = source.getClass();
        I18nClass i18nClass = sourceClass.getAnnotation(I18nClass.class);
        if (i18nClass == null) {
            return;
        }
        for (Field field : ReflectUtil.getFields(sourceClass)) {
            Object[] elements;
            Class<?> fieldType = field.getType();
            Object fieldValue = ReflectUtil.getFieldValue((Object)source, (Field)field);
            if (fieldValue instanceof String) {
                String string;
                I18nField i18nField = field.getAnnotation(I18nField.class);
                if (i18nField == null) continue;
                String conditionExpression = i18nField.condition();
                if (CharSequenceUtil.isNotEmpty((CharSequence)conditionExpression)) {
                    Expression expression = EXPRESSION_CACHE.computeIfAbsent(conditionExpression, arg_0 -> ((ExpressionParser)PARSER).parseExpression(arg_0));
                    Boolean needI18n = (Boolean)expression.getValue(source, Boolean.class);
                    if (needI18n != null && !needI18n.booleanValue()) continue;
                }
                if (CharSequenceUtil.isEmpty((CharSequence)(string = this.parseMessageCode(source, (String)fieldValue, i18nField)))) continue;
                Locale locale = LocaleContextHolder.getLocale();
                String message = this.codeToMessage(string, locale, (String)fieldValue, this.fallbackLocale);
                ReflectUtil.setFieldValue((Object)source, (Field)field, (Object)message);
                continue;
            }
            if (fieldValue instanceof Collection) {
                elements = (Object[])fieldValue;
                if (CollUtil.isEmpty((Collection)elements)) continue;
                for (Object e : elements) {
                    this.switchLanguage(e);
                }
                continue;
            }
            if (fieldType.isArray()) {
                elements = (Object[])fieldValue;
                if (elements == null || elements.length == 0) continue;
                for (Object element : elements) {
                    this.switchLanguage(element);
                }
                continue;
            }
            this.switchLanguage(fieldValue);
        }
    }

    private String parseMessageCode(Object source, String fieldValue, I18nField i18nField) {
        String codeExpression = i18nField.code();
        if (CharSequenceUtil.isEmpty((CharSequence)codeExpression)) {
            return fieldValue;
        }
        Expression expression = EXPRESSION_CACHE.computeIfAbsent(codeExpression, arg_0 -> ((ExpressionParser)PARSER).parseExpression(arg_0));
        return (String)expression.getValue(source, String.class);
    }

    private String codeToMessage(String code, Locale locale, String defaultMessage, Locale fallbackLocale) {
        try {
            String message = this.messageSource.getMessage(code, null, locale);
            return message;
        }
        catch (NoSuchMessageException e) {
            LogUtils.warn((String)"[codeToMessage]\u672a\u627e\u5230\u5bf9\u5e94\u7684\u56fd\u9645\u5316\u914d\u7f6e\uff0ccode: {}, local: {}", (Object[])new Object[]{code, locale});
            if (fallbackLocale != null && locale != fallbackLocale) {
                try {
                    String message = this.messageSource.getMessage(code, null, fallbackLocale);
                    return message;
                }
                catch (NoSuchMessageException e2) {
                    LogUtils.warn((String)"[codeToMessage]\u671f\u671b\u8bed\u8a00\u548c\u56de\u9000\u8bed\u8a00\u4e2d\u7686\u672a\u627e\u5230\u5bf9\u5e94\u7684\u56fd\u9645\u5316\u914d\u7f6e\uff0ccode: {}, local: {}, fallbackLocale\uff1a{}", (Object[])new Object[]{code, locale, fallbackLocale});
                }
            }
            if (this.useCodeAsDefaultMessage) {
                return code;
            }
            return defaultMessage;
        }
    }
}

