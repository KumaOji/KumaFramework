/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.support.tracker;

import cn.hutool.core.date.StopWatch;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * public List<User> listUsers(QueryWrapper<User> wrapper) { long startTime = System.currentTimeMillis(); List<User>
 * users = PerfTracker.tracker(() -> userMapper.selectList(wrapper)); long endTime = System.currentTimeMillis();
 * log.info("查询耗时：{}ms", (endTime - startTime)); return users; }
 */
public class PerfTracker {

    private static final Logger log = LoggerFactory.getLogger(PerfTracker.class);

    private final StopWatch stopWatch;

    private final String methodName;

    private PerfTracker( String methodName ) {
        this.stopWatch = new StopWatch();
        this.methodName = methodName;
    }

    public static <T> T tracker( Supplier<T> supplier ) {
        try (TimerContext timerContext = PerfTracker.start()) {
            timerContext.start();
            T result = supplier.get();
            timerContext.stop();
            return result;
        }
    }

    private static TimerContext start() {
        return new TimerContext(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * TimerContext
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    private static class TimerContext implements AutoCloseable {

        private final PerfTracker tracker;

        private TimerContext( String methodName ) {
            this.tracker = new PerfTracker(methodName);
        }

        private void start() {
            tracker.stopWatch.start();
        }

        private void stop() {
            tracker.stopWatch.stop();
        }

        @Override
        public void close() {
            long executeTime = tracker.stopWatch.getTotalTimeMillis();
            if (executeTime > 500) {
                log.warn("慢查询告警：方法 {} 耗时 {}ms", tracker.methodName, executeTime);
            }
        }
    }
}
