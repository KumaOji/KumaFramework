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
import java.util.Objects;

/**
 * 装饰器模式装饰Runnable，传递父线程的线程号
 */
public class MDCRunnable implements Runnable {

    private Runnable runnable;

    /**
     * 保存当前主线程的MDC值
     */
    private final Map<String, String> mainMdcMap;

    public MDCRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.mainMdcMap = MdcUtils.getCopyOfContextMap();
    }

    @Override
    public void run() {
        if (Objects.nonNull(mainMdcMap) && !mainMdcMap.isEmpty()) {
            MdcUtils.setContextMap(mainMdcMap);
        }

        try {
            runnable.run();
        } finally {
            if (Objects.nonNull(mainMdcMap) && !mainMdcMap.isEmpty()) {
                MdcUtils.clear();
            }
        }
    }
}
