/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.AsyncContext
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.http.HttpServlet
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
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

public class AsyncServlet
extends HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("GBK");
        response.setContentType("text/html;charset=GBK");
        AsyncContext actx = request.startAsync();
        actx.setTimeout(90000L);
        actx.start((Runnable)new MyThread(actx));
        PrintWriter out = response.getWriter();
        out.println("<h1>\u4e0d\u7b49\u5f02\u6b65\u8fd4\u56de\u7ed3\u679c\u5c31\u76f4\u63a5\u8fd4\u5230\u9875\u9762\u7684\u5185\u5bb9</h1>");
        out.flush();
    }

    public static class MyThread
    implements Runnable {
        private AsyncContext actx;

        public MyThread(AsyncContext actx) {
            this.actx = actx;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(4000L);
                ServletRequest req = this.actx.getRequest();
                req.setAttribute("content", (Object)"\u5f02\u6b65\u83b7\u5f97\u7684\u6570\u636e");
                this.actx.dispatch("/index.jsp");
            }
            catch (Exception e) {
                LogUtils.error((Throwable)e);
            }
        }
    }
}

