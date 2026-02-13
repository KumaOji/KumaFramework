/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.thread;

import com.kuma.boot.common.utils.servlet.MdcUtils;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

public class MDCCallable<T>
implements Callable<T> {
    private Callable<T> callable;
    private final Map<String, String> mainMdcMap;

    public MDCCallable(Callable<T> callable) {
        this.callable = callable;
        this.mainMdcMap = MdcUtils.getCopyOfContextMap();
    }

    @Override
    public T call() throws Exception {
        if (Objects.nonNull(this.mainMdcMap) && !this.mainMdcMap.isEmpty()) {
            MdcUtils.setContextMap(this.mainMdcMap);
        }
        try {
            T t = this.callable.call();
            return t;
        }
        finally {
            if (Objects.nonNull(this.mainMdcMap) && !this.mainMdcMap.isEmpty()) {
                MdcUtils.clear();
            }
        }
    }
}

