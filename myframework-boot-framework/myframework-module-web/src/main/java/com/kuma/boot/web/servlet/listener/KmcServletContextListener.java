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

package com.kuma.boot.web.servlet.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionListener;

/**
 * 标准servlet上下文侦听器
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-27 10:16:08
 */
// @WebListener("kmcServletContextListener")
public class KmcServletContextListener
        implements ServletContextListener,
        HttpSessionListener,
        HttpSessionAttributeListener,
        ServletRequestListener,
        ServletRequestAttributeListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String servletContextName = sce.getServletContext().getServletContextName();
        String serverInfo = sce.getServletContext().getServerInfo();
        int majorVersion = sce.getServletContext().getMajorVersion();
        int minorVersion = sce.getServletContext().getMinorVersion();
        String contextPath = sce.getServletContext().getContextPath();

        LogUtils.info(
                "KmcServletContextListener ServletContextName {} 容器启动 服务信息 {} {}-{}-{}",
                servletContextName,
                serverInfo,
                majorVersion,
                minorVersion,
                contextPath);
    }
}
