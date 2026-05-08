/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 */

package com.kuma.cloud.graalvmtest.aot;

import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;
import static org.springframework.aot.hint.MemberCategory.ACCESS_DECLARED_FIELDS;

import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/**
 * GraalVM / Spring AOT：banner、META-INF/services、MyBatis 日志实现反射；
 * Jackson JSR353 + Glassfish JSON-P 用 {@link TypeReference} 注册（classpath 上对子模块常为 runtime-only）。
 *
 * <p>另在 {@code application.yml} 中关闭 {@code spring.mvc.formcontent.filter.enabled}，避免仅有 FormContentFilter
 * 在 Native 中间接触发仍不兼容的 Provider 装载链。</p>
 */
public class GraalVmRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    private static final TypeReference JSR353_MODULE =
            TypeReference.of("tools.jackson.datatype.jsr353.JSR353Module");
    private static final TypeReference JSON_P_PROVIDER_IMPL =
            TypeReference.of("org.glassfish.json.JsonProviderImpl");

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("banner/kmc-banner.txt");
        hints.resources().registerPattern("logback-spring.xml");
        hints.resources().registerPattern("META-INF/services/javax.json.spi.JsonProvider");
        hints.resources().registerPattern("META-INF/services/tools.jackson.databind.JacksonModule");
        hints.reflection().registerType(Slf4jImpl.class, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(NoLoggingImpl.class, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(JSR353_MODULE, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(JSON_P_PROVIDER_IMPL, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection()
                .registerType(
                        TomcatServletWebServerFactory.class,
                        INVOKE_PUBLIC_METHODS,
                        INVOKE_DECLARED_CONSTRUCTORS,
                        ACCESS_DECLARED_FIELDS);
    }
}
