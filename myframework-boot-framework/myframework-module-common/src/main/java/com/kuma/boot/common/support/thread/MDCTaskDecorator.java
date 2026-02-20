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

import com.kuma.boot.common.utils.servlet.MdcUtils;
import java.util.Map;
import org.springframework.core.task.TaskDecorator;

/**
 * 对于异步任务时, 同样也能获取到 TraceId spring 的异步任务 @Async
 */
public class MDCTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        try {
            Map<String, String> previous = MdcUtils.getCopyOfContextMap();
            return () -> {
                try {
                    MdcUtils.setContextMap(previous);
                    runnable.run();
                } finally {
                    MdcUtils.clear();
                }
            };
        } catch (IllegalStateException e) {
            return runnable;
        }
    }
}
