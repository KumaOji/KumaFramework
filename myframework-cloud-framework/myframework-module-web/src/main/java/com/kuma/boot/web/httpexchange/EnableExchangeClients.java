/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.annotation.Import
 *  org.springframework.core.annotation.AliasFor
 */
package com.kuma.boot.web.httpexchange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Import(value={ExchangeClientsRegistrar.class})
public @interface EnableExchangeClients {
    @AliasFor(value="basePackages")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] basePackages() default {};

    public Class<?>[] clients() default {};
}

