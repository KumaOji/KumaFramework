/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.thread;

import com.kuma.boot.common.utils.servlet.MdcUtils;
import java.util.Map;
import java.util.Objects;

public class MDCRunnable
implements Runnable {
    private Runnable runnable;
    private final Map<String, String> mainMdcMap;

    public MDCRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.mainMdcMap = MdcUtils.getCopyOfContextMap();
    }

    @Override
    public void run() {
        if (Objects.nonNull(this.mainMdcMap) && !this.mainMdcMap.isEmpty()) {
            MdcUtils.setContextMap(this.mainMdcMap);
        }
        try {
            this.runnable.run();
        }
        finally {
            if (Objects.nonNull(this.mainMdcMap) && !this.mainMdcMap.isEmpty()) {
                MdcUtils.clear();
            }
        }
    }
}

