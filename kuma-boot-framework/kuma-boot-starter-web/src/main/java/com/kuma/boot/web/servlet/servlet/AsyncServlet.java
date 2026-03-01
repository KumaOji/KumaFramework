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
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

// @WebServlet(name = "AsyncServlet", urlPatterns = {"/testAsyn.do"}, asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 解决乱码
        request.setCharacterEncoding("GBK");
        response.setContentType("text/html;charset=GBK");
        // 通过request获得AsyncContent对象
        AsyncContext actx = request.startAsync(); //
        // 重点方法**
        // 设置异步调用超时时长
        actx.setTimeout(30 * 3000);
        // 启动异步调用的线程
        // 重点方法**
        actx.start(new MyThread(actx));

        // 直接输出到页面的内容(不等异步完成就直接给页面)
        // 但这些内容必须放在标签内,否则会在页面输出错误内容，这儿反正我测试是这样，具体不知对不对？？
        PrintWriter out = response.getWriter();
        out.println("<h1>不等异步返回结果就直接返到页面的内容</h1>");
        out.flush();
    }

    // 异步处理业务的线程类
    public static class MyThread implements Runnable {

        private AsyncContext actx;

        // 构造
        public MyThread(AsyncContext actx) {
            this.actx = actx;
        }

        public void run() {
            try {
                // 等待5秒，模拟处理耗时的业务
                Thread.sleep(4 * 1000);
                // 获得request对象，添加数据给页面
                ServletRequest req = actx.getRequest();
                req.setAttribute("content", "异步获得的数据");
                // 将请求dispath到index.jsp页面，该页面的session必须设为false
                actx.dispatch("/index.jsp");
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }
    }
}
