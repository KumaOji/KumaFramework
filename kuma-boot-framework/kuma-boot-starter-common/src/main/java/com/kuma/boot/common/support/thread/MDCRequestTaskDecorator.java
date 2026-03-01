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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 *
 * <h2>子线程上下文装饰器</h2>
 *
 * <p>
 * https://stackoverflow.com/questions/23732089/how-to-enable-request-scope-in-async-task-executor
 *
 * <p>
 * 传递：RequestAttributes and MDC and SecurityContext
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-27 16:21:37
 */
public class MDCRequestTaskDecorator extends AbstractContextDecorator {

    public MDCRequestTaskDecorator(
            boolean enableServletAsyncContext, Long servletAsyncContextTimeoutMillis) {
        super(enableServletAsyncContext, servletAsyncContextTimeoutMillis);
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        // Servlet上下文
        ServletRequestAttributes context =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        // 日志上下文
        Map<String, String> previous = MdcUtils.getCopyOfContextMap();

        // ServletAsyncContext-enable：异步上下文最长生命周期（最大阻塞父线程多久）
        enableServletAsyncContext(context);

        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(context);
                if (previous != null) {
                    MdcUtils.setContextMap(previous);
                }
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                MdcUtils.clear();

                // ServletAsyncContext-complete：完成异步请求处理并关闭响应流
                completeServletAsyncContext(context);
            }
        };
    }
}
