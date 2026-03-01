/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.annatations;

import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.multi.DingerConfigHandler;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface MultiDinger {
    public DingerType dinger();

    public Class<? extends DingerConfigHandler> handler();
}

