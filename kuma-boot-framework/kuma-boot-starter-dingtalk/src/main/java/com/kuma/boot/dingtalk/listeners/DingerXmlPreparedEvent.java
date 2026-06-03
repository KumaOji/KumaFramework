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

package com.kuma.boot.dingtalk.listeners;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.annatations.DingerScan;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

/**
 * DingerXmlPreparedEvent
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:21:43
 */
@Deprecated
public class DingerXmlPreparedEvent implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        LogUtils.info("ready to execute dinger analysis.");
        loadPrimarySources(event);
    }

    /**
     * loadPrimarySources
     *
     * @param event event {@link ApplicationEnvironmentPreparedEvent}
     */
    private void loadPrimarySources(ApplicationEnvironmentPreparedEvent event) {
        Set<?> allSources;
        if (SpringBootVersion.getVersion().startsWith("1.")) {
            allSources = event.getSpringApplication().getSources();
        } else {
            allSources = event.getSpringApplication().getAllSources();
        }
        Set<Class<?>> primarySources = new HashSet<>();
        for (Object source : allSources) {
            if (Class.class.isInstance(source)) {
                Class<?> clazz = (Class<?>) source;
                if (clazz.isAnnotationPresent(DingerScan.class)) {
                    primarySources.add(clazz);
                }
            }
        }
    }
}
