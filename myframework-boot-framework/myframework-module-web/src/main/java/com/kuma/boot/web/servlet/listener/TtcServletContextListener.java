/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.ServletContextEvent
 *  jakarta.servlet.ServletContextListener
 *  jakarta.servlet.ServletRequestAttributeListener
 *  jakarta.servlet.ServletRequestListener
 *  jakarta.servlet.http.HttpSessionAttributeListener
 *  jakarta.servlet.http.HttpSessionListener
 */
package com.kuma.boot.web.servlet.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionListener;

public class KmcServletContextListener
implements ServletContextListener,
HttpSessionListener,
HttpSessionAttributeListener,
ServletRequestListener,
ServletRequestAttributeListener {
    public void contextInitialized(ServletContextEvent sce) {
        String servletContextName = sce.getServletContext().getServletContextName();
        String serverInfo = sce.getServletContext().getServerInfo();
        int majorVersion = sce.getServletContext().getMajorVersion();
        int minorVersion = sce.getServletContext().getMinorVersion();
        String contextPath = sce.getServletContext().getContextPath();
        LogUtils.info((String)"KmcServletContextListener ServletContextName {} \u5bb9\u5668\u542f\u52a8 \u670d\u52a1\u4fe1\u606f {} {}-{}-{}", (Object[])new Object[]{servletContextName, serverInfo, majorVersion, minorVersion, contextPath});
    }
}

