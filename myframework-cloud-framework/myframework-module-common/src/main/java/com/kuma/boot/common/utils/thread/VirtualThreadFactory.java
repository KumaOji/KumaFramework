/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.NonNull
 */
package com.kuma.boot.common.utils.thread;

import java.util.concurrent.ThreadFactory;
import org.jspecify.annotations.NonNull;

final class VirtualThreadFactory
implements ThreadFactory {
    public static final VirtualThreadFactory INSTANCE = new VirtualThreadFactory();

    VirtualThreadFactory() {
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread thread = new Thread(r);
        return Thread.ofVirtual().name("kmc-virtual-" + thread.getName()).inheritInheritableThreadLocals(true).unstarted(r);
    }
}

