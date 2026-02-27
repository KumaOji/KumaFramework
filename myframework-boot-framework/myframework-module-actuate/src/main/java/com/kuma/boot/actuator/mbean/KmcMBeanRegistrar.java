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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * CustomMbeanRegistrar
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:08:05
 */
public class KmcMBeanRegistrar implements ApplicationContextAware, InitializingBean, DisposableBean {

    /** applicationContext */
    private ConfigurableApplicationContext applicationContext;
    /** objectName */
    private final ObjectName objectName =
            new ObjectName("com.kuma.boot.actuator.endpoint:type=KmcAdmin,name=SystemInfoMBean");

    public KmcMBeanRegistrar() throws MalformedObjectNameException {}

    @Override
    public void destroy() throws Exception {
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(this.objectName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.registerMBean(new com.kuma.boot.actuator.mbean.SystemInfo(), this.objectName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
