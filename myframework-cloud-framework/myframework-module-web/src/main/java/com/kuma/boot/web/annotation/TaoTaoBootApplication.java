/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.context.EnableContextUtils
 *  com.kuma.boot.security.spring.annotation.EnableOauth2ResourceServer
 *  org.springframework.boot.autoconfigure.SpringBootApplication
 *  org.springframework.boot.web.server.servlet.context.ServletComponentScan
 *  org.springframework.context.annotation.EnableAspectJAutoProxy
 */
package com.kuma.boot.web.annotation;

import com.kuma.boot.common.utils.context.EnableContextUtils;
import com.kuma.boot.security.spring.annotation.EnableOauth2ResourceServer;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@EnableContextUtils
@ServletComponentScan(basePackages={"com.kuma.boot.web.servlet"})
@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
@EnableOauth2ResourceServer
@SpringBootApplication
public @interface KumaBootApplication {
}

