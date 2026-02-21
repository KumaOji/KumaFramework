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

package com.kuma.boot.web.servlet.servlet;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 标准异步servlet
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-27 10:17:33
 */
// asyncSupported 表示本Servelt是否支持异步
// @WebServlet(urlPatterns = "/kmcAsyncServlet", asyncSupported = true, description = "异步servlet")
public class KmcAsyncServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AsyncContext asyncContext = req.startAsync();
        asyncContext.start(
                () -> {
                    try {
                        resp.getWriter().write("async : hello world!");
                        // 异步操作时候最终这里要进行结束，在这之前可以多次调用request和response：
                        // eg: asyncContext.getResponse().getWriter().write("hello");
                        asyncContext.complete();
                    } catch (IOException e) {
                        LogUtils.error(e);
                    }
                });
        // resp.getWriter().write("Hello World!");
    }
}
