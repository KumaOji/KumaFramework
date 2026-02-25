/*
 *  org.jspecify.annotations.NonNull
 *  org.springframework.context.annotation.ImportSelector
 *  org.springframework.core.type.AnnotationMetadata
 */
package com.kuma.boot.ratelimit.ratelimitenhance.selector;

import com.kuma.boot.ratelimit.ratelimitenhance.config.RedisLimitingAutoConfiguration;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class RedisLimitingImportSelector
implements ImportSelector {
    public @NonNull String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        return new String[]{RedisLimitingAutoConfiguration.class.getName()};
    }
}

