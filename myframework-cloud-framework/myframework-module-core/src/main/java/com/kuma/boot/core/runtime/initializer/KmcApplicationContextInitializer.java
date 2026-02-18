/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.core.runtime.initializer;

import static com.kuma.boot.common.constant.CommonConstants.ENV;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.core.enums.KmcEnvEnum;
import com.kuma.boot.core.autoconfigure.properties.CoreProperties;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 应用上下文初始化器
 *
 * IOC容器初始化时被回调 启动前环境检测
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:23:01
 */
@Order(0)
public class KmcApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {

        //LogUtils.started(KmcApplicationContextInitializer.class, StarterNameConstants.CORE_STARTER);

        if (context instanceof AnnotationConfigApplicationContext) {
            // AnnotationConfigApplicationContext annotationConfigApplicationContext =
            // (AnnotationConfigApplicationContext) context;
            // annotationConfigApplicationContext.register(Config.class);
        } else {
            if (ContextUtils.mainClass == null) {
                ContextUtils.mainClass = deduceMainApplicationClass();
            }
            ContextUtils.setApplicationContext(context);
            ConfigurableEnvironment environment = context.getEnvironment();

            String applicationName = environment.getProperty(CommonConstants.SPRING_APP_NAME_KEY);

            Boolean isEnabled = environment.getProperty(CoreProperties.PREFIX + ".enabled", Boolean.class);
            if (Boolean.FALSE.equals(isEnabled)) {
                return;
            }

            // 环境变量初始化
            String env = environment.getProperty(CoreProperties.PREFIX + ".env", String.class, KmcEnvEnum.DEV.name());

            if (!StringUtils.isEmpty(applicationName) && !StringUtils.isEmpty(env)) {
                optimize(environment);

                setProperty(CommonConstants.SPRING_APP_NAME_KEY, applicationName, "[kuma boot 环境变量]");

                for (KmcEnvEnum kmcEnvEnum : KmcEnvEnum.values()) {
                    if (kmcEnvEnum.toString().equalsIgnoreCase(env)) {
                        // 设置apollo env环境变量
                        setProperty(ENV, kmcEnvEnum.getName(), "[kuma boot 环境变量]");
                    }
                }
            }

            // 设置apollo env环境变量
            System.setProperty(ENV, KmcEnvEnum.getCurrEnv().name().toUpperCase());

            optimizeLog(environment);
        }
    }

    /**
     * deduceMainApplicationClass
     *
     * @return {@link Class }
     * @since 2021-09-02 20:23:23
     */
    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }

    /**
     * optimizeLog
     *
     * @param environment environment
     * @since 2021-09-02 20:23:27
     */
    private void optimizeLog(ConfigurableEnvironment environment) {
        String message = "[日志标准规范]";

        // 文件优化
        // setDefaultProperty(CoreProperties.LoggingFile, "log/app.log", message);
        // setDefaultProperty(CoreProperties.LoggingFileMaxHistory, "3", message);
        // setDefaultProperty(CoreProperties.LoggingFileMaxSize, "10MB", message);

        // 日志优化最大
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (factory instanceof LoggerContext) {
            Logger root = ((LoggerContext) factory).getLogger("ROOT");
            if (root != null) {
                Appender<ILoggingEvent> file = root.getAppender("FILE");
                if (file instanceof RollingFileAppender) {
                    RollingPolicy rollingPolicy = ((RollingFileAppender<?>) file).getRollingPolicy();
                    if (rollingPolicy instanceof SizeAndTimeBasedRollingPolicy) {
                        setProperty(CommonConstants.LOGGING_FILE_TOTAL_SIZE, "1GB", message);

                        ((SizeAndTimeBasedRollingPolicy<?>) rollingPolicy)
                                .setTotalSizeCap(FileSize.valueOf(
                                        environment.getProperty(CommonConstants.LOGGING_FILE_TOTAL_SIZE, "1GB")));
                    }
                }
            }
        }
    }

    /**
     * optimize
     *
     * @param environment environment
     * @since 2021-09-02 20:23:30
     */
    private void optimize(ConfigurableEnvironment environment) {
        // 启动全局优化默认配置
        double cpuCount = Runtime.getRuntime().availableProcessors();

        //if (RequestWebUtils.isWeb()) {
        // tomcat 优化 * 核心数
        // setDefaultProperty(CoreProperties.ServerTomcatMaxThreads, ((int) (200 * cpuCount)) +
        // "",
        //	"[自动化调优]");
        // setDefaultProperty(CoreProperties.ServerTomcatMaxConnections,
        //	((int) (10000 * cpuCount)) + "", "[自动化调优]");
        // setDefaultProperty(CoreProperties.ServerTomcatMinSpaceThreads,
        //	((int) (20 * (cpuCount / 2))) + "", "[自动化调优]");
        // tomcat 传输优化
        // setDefaultProperty(CoreProperties.ServeCompressionEnabled, "true", "[自动化调优]");
        // setDefaultProperty(CoreProperties.ServeCompressionMimeTypes,
        // "application/json,application/xml,text/html,text/xml,text/plain", "[自动化调优]");
        //}
        // setDefaultProperty("server.tomcat.accept-count","200");
        // setDefaultProperty("server.connection-timeout","");
    }

    /**
     * setDefaultProperty
     *
     * @param key key
     * @param defaultPropertyValue defaultPropertyValue
     * @param message message
     * @since 2021-09-02 20:23:33
     */
    private void setDefaultProperty(String key, String defaultPropertyValue, String message) {
        PropertyUtils.setDefaultInitProperty(
                KmcApplicationContextInitializer.class,
                PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY),
                key,
                defaultPropertyValue,
                message);
    }

    private void setProperty(String key, String propertyValue, String message) {
        PropertyUtils.setProperty(key, propertyValue, message);
    }
}
