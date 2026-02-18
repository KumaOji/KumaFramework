/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.core.support;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 全局进程关闭事件定义
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:36:39
 */
public class ShutdownHooks {

    /**
     * callBackList
     */
    private static final List<ShutdownHookHandler> hookHandlersList = new CopyOnWriteArrayList<>();
    private static final AtomicBoolean exectued = new AtomicBoolean(false);
    /**
     * lock
     */
    private static final Object LOCK = new Object();

    /**
     * 越大越晚 必须大于0
     *
     * @since 2021-09-02 20:37:02
     */
    public static void register(ShutdownHookHandler shutdownHookHandler) {
        synchronized (LOCK) {
            hookHandlersList.add(shutdownHookHandler);
        }
    }

    static {
        // JVM 停止或重启时
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                synchronized (LOCK) {
                    if (exectued.compareAndSet(false, true)) {
                        OrderComparator.sort(hookHandlersList);
                    }

                    LogUtils.info("kuma boot shutdown hooks start");

                    hookHandlersList.forEach(ShutdownHookHandler::beforeRunHook);

                    for (ShutdownHookHandler shutdownHookHandler : hookHandlersList) {
                        try {
                            shutdownHookHandler.runHook();
                        } catch (Exception e) {
                            LogUtils.error(e, "kuma boot shutdown hook 执行失败 {}", shutdownHookHandler.description());
                        }
                    }

                    LogUtils.info("kuma boot shutdown hooks end {}", "应用已正常退出!");
                }
            } catch (Exception e) {
                LogUtils.error(" 进程关闭事件回调处理出错", e);
            }
        },"ttc-shutdown"));
    }


    public static interface ShutdownHookHandler extends Ordered {
        default void beforeRunHook() {
            LogUtils.info("kuma boot beforeShutdownHook {}", description());
        }

        default void runHook() throws Exception {
            LogUtils.info("kuma boot shutdownHook {}", description());
        }

        String description();
    }

}
