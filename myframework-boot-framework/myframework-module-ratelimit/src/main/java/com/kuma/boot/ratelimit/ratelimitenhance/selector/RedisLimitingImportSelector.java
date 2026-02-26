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

package com.kuma.boot.ratelimit.ratelimitenhance.selector;

import com.kuma.boot.ratelimit.ratelimitenhance.config.RedisLimitingAutoConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.jspecify.annotations.NonNull;

/** redis限流组件导入selector */
public class RedisLimitingImportSelector implements ImportSelector {

    @NonNull
    @Override
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[] {RedisLimitingAutoConfiguration.class.getName()};
    }
}
