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

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * KmcRuntimeHints
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class KmcRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints( RuntimeHints hints, ClassLoader classLoader ) {

        // hints.reflection().registerConstructor(SimpleHelloService.class.getConstructors()[0],
        // ExecutableMode.INVOKE)
        //	.registerMethod(ReflectionUtils.findMethod(SimpleHelloService.class,"sayHello",String.class),
        //		ExecutableMode.INVOKE);
        // hints.resources().registerPattern("hello.txt");

        LogUtils.info("StandardRuntimeHints RuntimeHintsRegistrar");
    }
}
