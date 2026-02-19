/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.web.request.altas.annotation;

import com.kuma.boot.web.request.altas.config.AtlasLogAnnotationConfigRegistrar;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Import(value={AtlasLogAnnotationConfigRegistrar.class})
public @interface EnableAtlasLog {
    public boolean enabled() default true;

    public String defaultLevel() default "INFO";

    public String dateFormat() default "yyyy-MM-dd HH:mm:ss.SSS";

    public boolean prettyPrint() default false;

    public int maxMessageLength() default 2000;

    public boolean spelEnabled() default true;

    public boolean conditionEnabled() default true;

    public String[] enabledTags() default {"business", "security", "api"};

    public String[] enabledGroups() default {"default", "business"};

    public String[] exclusions() default {"*.toString", "*.hashCode", "*.equals"};

    public AtlasLogTrace trace() default @AtlasLogTrace;

    public AtlasLogPerformance performance() default @AtlasLogPerformance;

    public AtlasLogCondition condition() default @AtlasLogCondition;

    public AtlasLogSensitive sensitive() default @AtlasLogSensitive;

    public AtlasLogHttpLog httpLog() default @AtlasLogHttpLog;

    public AtlasLogResultLog resultLog() default @AtlasLogResultLog;
}

