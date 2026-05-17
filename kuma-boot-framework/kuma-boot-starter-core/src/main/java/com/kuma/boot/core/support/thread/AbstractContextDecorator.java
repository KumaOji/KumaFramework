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

package com.kuma.boot.core.support.thread;


import static cn.hutool.core.convert.Convert.toLong;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 *
 * <h2>子线程上下文装饰器</h2>
 *
 * <p>
 * <a href=
 * "https://stackoverflow.com/questions/23732089/how-to-enable-request-scope-in-async-task-executor">...</a>
 *
 * <p>
 * 传递：RequestAttributes and MDC and SecurityContext
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-27 16:21:33
 */
public abstract class AbstractContextDecorator implements TaskDecorator {

    /** ServletAsyncContext阻塞超时时长 setAttribute 时所使用的固定变量名 */
    public static final String SERVLET_ASYNC_CONTEXT_TIMEOUT_MILLIS =
            "servletAsyncContextTimeoutMillis";

    private boolean enableServletAsyncContext = false;

    private Long servletAsyncContextTimeoutMillis = 600L;

    public AbstractContextDecorator(
            boolean enableServletAsyncContext, Long servletAsyncContextTimeoutMillis) {
        this.enableServletAsyncContext = enableServletAsyncContext;
        this.servletAsyncContextTimeoutMillis = servletAsyncContextTimeoutMillis;
    }

    /**
     * 启用 ServletAsyncContext，异步上下文最长生命周期（最大阻塞父线程多久）
     *
     * <p>
     * 用于阻塞父线程 Servlet 的关闭（调用 destroy() 方法），导致子线程获取的上下文为空
     * @param context 父线程上下文
     */
    protected void enableServletAsyncContext(ServletRequestAttributes context) {
        if (!enableServletAsyncContext) {
            return;
        }

        HttpServletRequest request = context.getRequest();
        request.startAsync();
        Object servletAsyncContextTimeoutMillis =
                request.getAttribute(SERVLET_ASYNC_CONTEXT_TIMEOUT_MILLIS);
        if (servletAsyncContextTimeoutMillis == null) {
            servletAsyncContextTimeoutMillis = this.servletAsyncContextTimeoutMillis;
        }

        request.getAsyncContext().setTimeout(toLong(servletAsyncContextTimeoutMillis));
    }

    /**
     * 完成异步请求处理并关闭响应流
     * @param context 父线程上下文
     */
    protected void completeServletAsyncContext(ServletRequestAttributes context) {
        if (enableServletAsyncContext) {
            context.getRequest().getAsyncContext().complete();
        }
    }
}
