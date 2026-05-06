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

package com.kuma.boot.core.runtime.aot;

import static org.springframework.aot.hint.MemberCategory.ACCESS_DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;

import com.kuma.boot.core.autoconfigure.properties.CoreProperties;
import com.kuma.boot.core.enums.KmcEnvEnum;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * GraalVM Native / Spring AOT hints for kuma-boot-starter-core configuration binding.
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class KmcRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Native Image：StartupSpringApplication#setKmcBanner 使用 ResourceBanner 加载该文件；未注册则从镜像中缺失，回退为默认 Spring Boot ASCII banner
        hints.resources().registerPattern("banner/kmc-banner.txt");

        hints.reflection()
                .registerType(
                        CoreProperties.class,
                        INVOKE_PUBLIC_METHODS,
                        INVOKE_DECLARED_CONSTRUCTORS,
                        ACCESS_DECLARED_FIELDS);
        hints.reflection()
                .registerType(
                        KmcEnvEnum.class,
                        INVOKE_PUBLIC_METHODS,
                        INVOKE_DECLARED_CONSTRUCTORS,
                        ACCESS_DECLARED_FIELDS);
    }
}
