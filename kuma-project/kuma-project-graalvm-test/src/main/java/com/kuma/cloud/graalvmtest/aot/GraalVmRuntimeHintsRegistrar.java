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

import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * GraalVM / Spring AOT：把 KMC banner 等资源打进镜像，并为 MyBatis 日志适配器注册构造器反射，
 * 避免 Native 里 {@link org.apache.ibatis.logging.LogFactory} 无法建立 {@code logConstructor}
 * 而导致 {@link org.mybatis.spring.mapper.ClassPathMapperScanner} 初始化失败。
 */
public class GraalVmRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("banner/kmc-banner.txt");
        hints.resources().registerPattern("logback-spring.xml");
        hints.reflection().registerType(Slf4jImpl.class, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(NoLoggingImpl.class, INVOKE_DECLARED_CONSTRUCTORS);
    }
}
