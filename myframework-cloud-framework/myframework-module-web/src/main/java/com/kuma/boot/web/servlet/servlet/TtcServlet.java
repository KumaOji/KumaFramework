/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.http.HttpServlet
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 */
package com.kuma.boot.web.servlet.servlet;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class KmcServlet
extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Enumeration enu = req.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String)enu.nextElement();
            LogUtils.info((String)(paraName + ": " + req.getParameter(paraName)), (Object[])new Object[0]);
        }
        resp.getWriter().write("Hello Servlet");
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }
}

