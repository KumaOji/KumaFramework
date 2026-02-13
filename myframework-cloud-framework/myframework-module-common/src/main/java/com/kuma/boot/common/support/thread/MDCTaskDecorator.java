/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.TaskDecorator
 */
package com.kuma.boot.common.support.thread;

import com.kuma.boot.common.utils.servlet.MdcUtils;
import java.util.Map;
import org.springframework.core.task.TaskDecorator;

public class MDCTaskDecorator
implements TaskDecorator {
    public Runnable decorate(Runnable runnable) {
        try {
            Map<String, String> previous = MdcUtils.getCopyOfContextMap();
            return () -> {
                try {
                    MdcUtils.setContextMap(previous);
                    runnable.run();
                }
                finally {
                    MdcUtils.clear();
                }
            };
        }
        catch (IllegalStateException e) {
            return runnable;
        }
    }
}

