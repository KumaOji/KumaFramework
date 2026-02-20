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

package com.kuma.boot.common.support.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.util.StringUtils;

/**
 * ThreadFactoryCreator
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:28:58
 */
public final class ThreadFactoryCreator {

    public static ThreadFactory create( String threadName ) {
        if (!StringUtils.hasText(threadName)) {
            throw new IllegalArgumentException("argument [threadName] must not be blank");
        }
        return new NamedWithIdThreadFactory(threadName);
    }

    /**
     * NamedWithIdThreadFactory
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    private static final class NamedWithIdThreadFactory implements ThreadFactory {

        private final AtomicInteger threadId = new AtomicInteger(1);

        private final String namePrefix;

        private NamedWithIdThreadFactory( String namePrefix ) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread( Runnable command ) {
            Thread thread = new Thread(command);
            thread.setName(this.namePrefix + "-" + this.threadId.getAndAdd(1));
            return thread;
        }
    }
}
