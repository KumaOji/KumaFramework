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

package com.kuma.boot.actuator.mbean;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * 将 {@link SystemInfo} 注册到平台 MBeanServer，应用关闭时自动注销。
 *
 * @author kuma
 * @since 2021-09-02
 */
public class KmcMBeanRegistrar implements InitializingBean, DisposableBean {

    private final ObjectName objectName =
            new ObjectName("com.kuma.boot.actuator.endpoint:type=KmcAdmin,name=SystemInfoMBean");

    public KmcMBeanRegistrar() throws MalformedObjectNameException {}

    @Override
    public void afterPropertiesSet() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.registerMBean(new SystemInfo(), objectName);
    }

    @Override
    public void destroy() throws Exception {
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(objectName);
    }
}
