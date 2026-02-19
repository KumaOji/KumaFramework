/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.boot.autoconfigure.condition.ConditionOutcome
 *  org.springframework.boot.autoconfigure.condition.SpringBootCondition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.kuma.boot.web.request.annotation;

import com.kuma.boot.web.request.enums.RequestLoggerTypeEnum;
import com.kuma.boot.web.request.properties.RequestLoggerProperties;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
@Documented
@Conditional(value={RequestLogTypeCondition.class})
public @interface ConditionalOnRequestLogger {
    public RequestLoggerTypeEnum logType() default RequestLoggerTypeEnum.LOGGER;

    public static class RequestLogTypeCondition
    extends SpringBootCondition {
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            RequestLoggerProperties properties;
            try {
                properties = (RequestLoggerProperties)Objects.requireNonNull(context.getBeanFactory()).getBean(RequestLoggerProperties.class);
            }
            catch (BeansException e) {
                return new ConditionOutcome(false, "");
            }
            Map annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnRequestLogger.class.getName());
            assert (annotationAttributes != null);
            RequestLoggerTypeEnum requestLoggerTypeEnum = (RequestLoggerTypeEnum)((Object)annotationAttributes.get("logType"));
            RequestLoggerTypeEnum[] types = properties.getTypes();
            boolean b = Arrays.stream(types).anyMatch(type -> type.name().equals(requestLoggerTypeEnum.name()));
            return new ConditionOutcome(b, "");
        }
    }
}

