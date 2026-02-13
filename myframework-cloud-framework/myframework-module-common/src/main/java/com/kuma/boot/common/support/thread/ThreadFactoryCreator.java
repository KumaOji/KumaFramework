/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.common.support.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.util.StringUtils;

public final class ThreadFactoryCreator {
    public static ThreadFactory create(String threadName) {
        if (!StringUtils.hasText((String)threadName)) {
            throw new IllegalArgumentException("argument [threadName] must not be blank");
        }
        return new NamedWithIdThreadFactory(threadName);
    }

    private static final class NamedWithIdThreadFactory
    implements ThreadFactory {
        private final AtomicInteger threadId = new AtomicInteger(1);
        private final String namePrefix;

        private NamedWithIdThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable command) {
            Thread thread = new Thread(command);
            thread.setName(this.namePrefix + "-" + this.threadId.getAndAdd(1));
            return thread;
        }
    }
}

