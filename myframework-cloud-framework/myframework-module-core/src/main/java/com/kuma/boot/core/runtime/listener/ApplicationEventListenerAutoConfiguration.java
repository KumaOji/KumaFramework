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

package com.kuma.boot.core.runtime.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.runtime.report.ApplicationEventConfigurationListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

/**
 * SpringApplication的生命周期事件一共有七个，它们发生的顺序如下：
 *
 * <pre class="code">
 * ApplicationStartingEvent 这个事件是在一个SpringApplication对象的初始化和监听器的注册之后，抛出的。
 *
 * ApplicationEnvironmentPreparedEvent 这个事件在Environment对象创建之后，Context对象创建之前，抛出。
 *
 * ApplicationContextInitializedEvent 这个事件在ApplicationContext对象被初始化后抛出，抛出的时候，所有bean的定义还没被加载
 * 。
 * ApplicationPreparedEvent 这个事件在bean定义被加载之后，Context对象刷新之前抛出。
 *
 * ApplicationStartedEvent 这个事件在Context对象刷新之后，应用启动器被调用之前抛出。
 *
 * ApplicationReadyEvent 这个事件在应用启动器被调用之后抛出，这个代表着应用已经被正常的启动，可以接收请求了。
 *
 * ApplicationFailedEvent 这个事件只有在应用启动出现异常，无法正常启动时才会抛出。
 * </pre>
 */
@AutoConfiguration
@Import({
        ApplicationFailedEventListener.class,
        ApplicationReadyListener.class,
        ApplicationStartedListener.class,
        ContextInitializedListener.class,
        ApplicationEventConfigurationListener.class
})
public class ApplicationEventListenerAutoConfiguration {

    /**
     * ApplicationStartingEventListenerConfig
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    @Configuration
    public static class ApplicationStartingEventListenerConfig implements
            ApplicationListener<ApplicationStartingEvent> {

        @Override
        public void onApplicationEvent( ApplicationStartingEvent event ) {
            LogUtils.info(
                    "SpringApplicationEventListener  ApplicationStartingEvent" + " onApplicationEvent {}", event);
        }
    }

    /**
     * ApplicationEnvironmentPreparedEventListenerConfig
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    @Configuration
    public static class ApplicationEnvironmentPreparedEventListenerConfig implements
            ApplicationListener<ApplicationEnvironmentPreparedEvent> {

        @Override
        public void onApplicationEvent( ApplicationEnvironmentPreparedEvent event ) {
            LogUtils.info(
                    "SpringApplicationEventListener ApplicationEnvironmentPreparedEvent"
                            + " onApplicationEvent {}",
                    event);
        }
    }

    /**
     * ApplicationContextInitializedEventListenerConfig
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    @Configuration
    public static class ApplicationContextInitializedEventListenerConfig implements
            ApplicationListener<ApplicationContextInitializedEvent> {

        @Override
        public void onApplicationEvent( ApplicationContextInitializedEvent event ) {
            LogUtils.info(
                    "SpringApplicationEventListener ApplicationContextInitializedEvent"
                            + " onApplicationEvent {}",
                    event);
        }
    }

    /**
     * ApplicationPreparedEventListenerConfig
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    @Configuration
    public static class ApplicationPreparedEventListenerConfig implements
            ApplicationListener<ApplicationPreparedEvent> {

        @Override
        public void onApplicationEvent( ApplicationPreparedEvent event ) {
            LogUtils.info(
                    "SpringApplicationEventListener ApplicationPreparedEvent" + " onApplicationEvent {}", event);
        }
    }

    /**
     * ApplicationStartedEventListenerConfig
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    @Configuration
    public static class ApplicationStartedEventListenerConfig implements ApplicationListener<ApplicationStartedEvent> {

        @Override
        public void onApplicationEvent( ApplicationStartedEvent event ) {
            Duration timeTaken = event.getTimeTaken();
            //todo 其他地方实现了
            //LogUtils.info("ApplicationEventListenerAutoConfiguration application Started timeTaken: {} s", timeTaken.toSeconds());
            //
            //LogUtils.info(
            //	"SpringApplicationEventListener ApplicationStartedEvent" + " onApplicationEvent {}", event);
        }
    }

    /**
     * ApplicationReadyEventListenerConfig
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    @Configuration
    public static class ApplicationReadyEventListenerConfig implements ApplicationListener<ApplicationReadyEvent> {

        @Override
        public void onApplicationEvent( ApplicationReadyEvent event ) {
            Duration timeTaken = event.getTimeTaken();
            //todo 其他地方有实现了
            //LogUtils.info("ApplicationEventListenerAutoConfiguration application Ready timeTaken: {} s",
            //	timeTaken.toSeconds());
            //
            //LogUtils.info("SpringApplicationEventListener ApplicationReadyEvent onApplicationEvent" + " {}",
            //	event);
        }
    }

    /**
     * ApplicationFailedEventListenerConfig
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    @Configuration
    public static class ApplicationFailedEventListenerConfig implements ApplicationListener<ApplicationFailedEvent> {

        @Override
        public void onApplicationEvent( ApplicationFailedEvent event ) {
            LogUtils.info(
                    "SpringApplicationEventListener  ApplicationFailedEvent onApplicationEvent" + " {}", event);
        }
    }
}
