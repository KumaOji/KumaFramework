/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.springdoc.knife4j.spring.annotations;

import com.kuma.boot.springdoc.knife4j.spring.configuration.Knife4jAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
@Import(value={Knife4jAutoConfiguration.class})
@ConditionalOnWebApplication
public @interface EnableKnife4j {
}

