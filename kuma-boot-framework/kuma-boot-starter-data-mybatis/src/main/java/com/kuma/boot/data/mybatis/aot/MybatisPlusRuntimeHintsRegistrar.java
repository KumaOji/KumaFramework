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

package com.kuma.boot.data.mybatis.aot;

import static org.springframework.aot.hint.MemberCategory.ACCESS_DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.kuma.boot.data.mybatis.mybatisplus.handler.objecthandler.AutoFieldMetaObjectHandler;
import com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler.MybatisEnumTypeHandler;
import com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler.like.FullLikeTypeHandler;
import com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler.like.LeftLikeTypeHandler;
import com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler.like.RightLikeTypeHandler;
import com.kuma.boot.data.mybatis.mybatisplus.incrementer.SnowFlakeIdGenerator;
import com.kuma.boot.data.mybatis.mybatisplus.injector.MateSqlInjector;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.MpInterceptor;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/**
 * GraalVM Native Image hints for MyBatis / MyBatis-Plus integration.
 *
 * <p>Framework mapper 包由 {@link com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusAutoConfiguration}
 * 的 {@code @MapperScan} 在 AOT 编译期解析；应用业务 Mapper 仍须在启动类上对 {@code com.kuma.cloud.*.mapper}
 * 等包显式 {@code @MapperScan}。
 */
public class MybatisPlusRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    /** 与 starter 无编译依赖，按 FQCN 注册（Native 下 Mapper 接口方法需可代理） */
    private static final TypeReference BUSINESS_IDEMPOTENT_MAPPER =
            TypeReference.of("com.kuma.boot.idempotent.idempotentenhance.db.mapper.BusinessIdempotentMapper");

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ReflectionHints reflection = hints.reflection();
        register(reflection, MapperProxy.class);
        register(reflection, MapperProxyFactory.class);
        register(reflection, MapperMethod.class);
        register(reflection, MapperRegistry.class);
        // Native image：LogFactory 通过 Slf4jImpl(String) 探测日志实现，须注册构造函数反射
        register(reflection, Slf4jImpl.class);
        register(reflection, NoLoggingImpl.class);
        register(reflection, DefaultObjectFactory.class);
        register(reflection, DefaultObjectWrapperFactory.class);
        // MyBatis Javassist 帮助类在 ibatis 包内非 public，只能按名称注册 AOT hint
        registerReflectionName(
                reflection, "org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory");
        registerReflectionName(
                reflection, "org.apache.ibatis.executor.loader.javassist.JavassistSerialStateHolder");
        register(reflection, SqlSessionFactory.class);
        register(reflection, MybatisSqlSessionFactoryBean.class);
        register(reflection, MateSqlInjector.class);
        register(reflection, MpInterceptor.class);
        register(reflection, AutoFieldMetaObjectHandler.class);
        register(reflection, SnowFlakeIdGenerator.class);
        register(reflection, DefaultIdentifierGenerator.class);
        register(reflection, IdentifierGenerator.class);
        register(reflection, LeftLikeTypeHandler.class);
        register(reflection, RightLikeTypeHandler.class);
        register(reflection, FullLikeTypeHandler.class);
        register(reflection, MybatisEnumTypeHandler.class);

        reflection.registerType(BUSINESS_IDEMPOTENT_MAPPER, INVOKE_PUBLIC_METHODS, INVOKE_DECLARED_CONSTRUCTORS);

        hints.resources().registerPattern("mapper/*.xml");
        hints.resources().registerPattern("mapper/**/*.xml");
        hints.resources().registerPattern("mybatis/**/*.xml");
    }

    private static void register(ReflectionHints reflection, Class<?> type) {
        reflection.registerType(
                type,
                INVOKE_PUBLIC_METHODS,
                INVOKE_DECLARED_CONSTRUCTORS,
                ACCESS_DECLARED_FIELDS);
    }

    private static void registerReflectionName(ReflectionHints reflection, String className) {
        reflection.registerType(
                TypeReference.of(className),
                INVOKE_PUBLIC_METHODS,
                INVOKE_DECLARED_CONSTRUCTORS,
                ACCESS_DECLARED_FIELDS);
    }
}
