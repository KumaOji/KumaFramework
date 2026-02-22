/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.security.spring.annotation;

import com.kuma.boot.security.spring.configuration.JwtDecoderAutoConfiguration;
import com.kuma.boot.security.spring.configuration.Oauth2ResourceAutoConfiguration;
import com.kuma.boot.security.spring.configuration.ReactiveJwtDecoderAutoConfiguration;
import com.kuma.boot.security.spring.configuration.SecurityAccessAutoConfiguration;
import com.kuma.boot.security.spring.configuration.SecurityAuthenticationAutoConfiguration;
import com.kuma.boot.security.spring.configuration.SecurityAuthorizationAutoConfiguration;
import com.kuma.boot.security.spring.configuration.cloud.JwtCloudAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
@Import(value={JwtCloudAutoConfiguration.class, JwtDecoderAutoConfiguration.class, ReactiveJwtDecoderAutoConfiguration.class, SecurityAccessAutoConfiguration.class, SecurityAuthorizationAutoConfiguration.class, Oauth2ResourceAutoConfiguration.class, SecurityAuthenticationAutoConfiguration.class})
public @interface EnableOauth2ResourceServer {
}

