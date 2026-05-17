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

package com.kuma.boot.core.runtime.initializer;

import static com.kuma.boot.common.constant.CommonConstants.ACTIVE_PROFILES_PROPERTY;
import static com.kuma.boot.common.constant.CommonConstants.SPRING_APP_NAME_KEY;
import static com.kuma.boot.common.constant.CommonConstants.KMC_ENV;
import static com.kuma.boot.common.constant.CommonConstants.KMC_ENV_VERSION;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.model.Callable;
import com.kuma.boot.core.utils.common.PropertyUtils;
import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.core.enums.KmcEnvEnum;
import com.kuma.boot.core.holder.RuntimeContextHolder;
import com.kuma.boot.core.support.ComponentCheck;
import com.kuma.boot.core.support.ShutdownHooks;
import com.kuma.boot.common.support.StatusListener;
import com.kuma.boot.common.support.KmcApplicationStatus;
import com.kuma.boot.common.support.version.KmcVersion;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.bootstrap.ConfigurableBootstrapContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * 在springcloud环境执行顺序 构造器  1  KumaCloudSysApplication starting 1 KumaCloudSysApplication
 * <p>
 * 构造器  1  BootstrapImportSelectorConfiguration starting 1 BootstrapImportSelectorConfiguration
 * <p>
 * environmentPrepared 1 BootstrapImportSelectorConfiguration contextPrepared 1
 * BootstrapImportSelectorConfiguration contextLoaded 1 BootstrapImportSelectorConfiguration started
 * 1 BootstrapImportSelectorConfiguration
 * <p>
 * environmentPrepared 1 KumaCloudSysApplication-BootstrapImportSelectorConfiguration
 * contextPrepared 1 KumaCloudSysApplication-BootstrapImportSelectorConfiguration contextLoaded 1
 * KumaCloudSysApplication-BootstrapImportSelectorConfiguration
 */
public class KmcSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {

    /**
     * runtimeContextHolder
     */
    private final RuntimeContextHolder runtimeContextHolder;
    private List<StatusListener> statusListenerList = new ArrayList<>();

    public KmcSpringApplicationRunListener(SpringApplication springApplication, String[] args) {
        //LogUtils.started(KmcSpringApplicationRunListener.class, StarterName.CORE_STARTER);

        //LogUtils.info("----1 KmcSpringApplicationRunListener 构造器, springApplication: {}", springApplication);

        System.setProperty(KMC_ENV_VERSION, KmcVersion.getVersion());

        Thread.currentThread().setName(CommonConstants.KMC_MAIN);

        runtimeContextHolder = RuntimeContextHolder.fromSpringApplication(springApplication, args);

        springApplication.setHeadless(true);

        springApplication.setRegisterShutdownHook(true);

        setBasePackages(springApplication);

        defaultSetting();

        createComponent();
    }

    // SpringApplication被初始化事件 run方法开始执行 发布ApplicationStartingEvent事件
    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        //LogUtils.info("----2 KmcSpringApplicationRunListener starting, ConfigurableBootstrapContext:{}", bootstrapContext);

        change(StatusEnum.STARTING, null);
    }

    //环境准备好时，发布ApplicationEnvironmentPreparedEvent事件
    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
                                    ConfigurableEnvironment environment) {
        //LogUtils.info("----3 KmcSpringApplicationRunListener environmentPrepared, bootstrapContext:{}", bootstrapContext);

        runtimeContextHolder.setEnvironment(environment);

        String jdbcUrl = environment.getProperty("spring.datasource.url");
        if (StrUtil.isNotBlank(jdbcUrl)) {
            //Jdbcs.get
        }
        excludeAutoConfiguration(environment);

        String property = environment.getProperty(ACTIVE_PROFILES_PROPERTY);
        if (StrUtil.isNotBlank(property)) {
            System.setProperty(KMC_ENV, property.toUpperCase());
        }

        KmcEnvEnum.getEnv();


    }

    //ApplicationContext初始化后，bean还没初始化 一旦创建并准备好ApplicationContext，在加载源之前调用。
    // 容器的上下文准备初始化完毕 发布ApplicationContextInitializedEvent事件
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        //LogUtils.info("----4 KmcSpringApplicationRunListener contextPrepared, ConfigurableApplicationContext:{}", context);

        runtimeContextHolder.setApplicationContext(context);

    }

    //bean被定义后，context对象刷新之前 在应用程序上下文加载后但在刷新之前调用。 发布ApplicationPreparedEvent事件
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        //LogUtils.info("----5 KmcSpringApplicationRunListener contextLoaded, ConfigurableApplicationContext:{}", context);

        List<ComponentCheck> componentCheckList = SpringFactoriesLoader.loadFactories(
                ComponentCheck.class,
                Thread.currentThread().getContextClassLoader());
        if (CollUtil.isNotEmpty(componentCheckList)) {
            componentCheckList.forEach((e) -> e.checkBeforeContextRefresh(context));
        }
    }

    //Context被刷新之后，启动器被调用之前 上下文已刷新，应用程序已启动，但尚未调用CommandLineRunners和ApplicationRunners。
    //发布ApplicationStartedEvent事件
    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        //LogUtils.info("----6 KmcSpringApplicationRunListener started, ConfigurableApplicationContext:{}", context);

        change(StatusEnum.STARTED, () -> {
            ShutdownHooks.register(new ShutdownHooks.ShutdownHookHandler() {
                @Override
                public int getOrder() {
                    return 0;
                }

                @Override
                public void runHook() throws Exception {
                    change(StatusEnum.STOPPING, () -> {
                        LogUtils.info("kuma boot {}", "应用退出中...");
                    });
                }

                @Override
                public String description() {
                    return "应用退出中";
                }
            });

            ShutdownHooks.register(new ShutdownHooks.ShutdownHookHandler() {
                @Override
                public int getOrder() {
                    return Integer.MAX_VALUE;
                }

                @Override
                public void runHook() throws Exception {
                    change(StatusEnum.STOPPED, () -> {
                        LogUtils.info("kuma boot {}", "应用已正常退出!");
                    });
                }

                @Override
                public String description() {
                    return "应用已正常退出";
                }
            });
        });
    }

    //启动器调用后，整个SpringBoot启动成功 当应用程序上下文已刷新并且所有CommandLineRunners和ApplicationRunners已调用时，在run方法完成之前立即调用。
    //发布ApplicationReadyEvent事件
    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        //LogUtils.info("----7 KmcSpringApplicationRunListener ready, ConfigurableApplicationContext:{}", context);

    }

    //应用启动出现异常 当运行应用程序时发生故障时调用。 发布ApplicationFailedEvent事件
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        //LogUtils.info("----8 KmcSpringApplicationRunListener failed, ConfigurableApplicationContext:{}", context);

        change(StatusEnum.FAILED, () -> {
            LogUtils.info("kuma boot {}", "", "应用启动失败!");
        });
    }


    @Override
    public int getOrder() {
        return 0;
    }

    public void change(StatusEnum status, Callable.Action0 action0) {
        if (ContextUtils.getApplicationContext() != null && !StrUtil.isEmpty(
                ContextUtils.getApplicationContext().getEnvironment()
                        .getProperty(SPRING_APP_NAME_KEY))) {
            KmcApplicationStatus.Current = status;

            for (StatusListener l : statusListenerList) {
                l.onApplicationEvent(status);
            }

            if (action0 != null) {
                action0.invoke();
            }
        }
    }

    private void setBasePackages(SpringApplication springApplication) {
        Set<String> basePackages = new HashSet<>();
        for (Object source : springApplication.getAllSources()) {
            Package aPackage = ((Class<?>) source).getPackage();
            basePackages.add(aPackage.getName());
        }
        runtimeContextHolder.setBasePackages(basePackages);
    }

    private void defaultSetting() {
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);

        PropertyUtils.setProperty("kmc-cloud", Boolean.TRUE.toString(), "");
        PropertyUtils.setProperty("spring.aop.proxy-target-class", Boolean.TRUE.toString(), "");
        PropertyUtils.setProperty("server.server-header", Boolean.TRUE.toString(), "");
        PropertyUtils.setProperty("logging.register-shutdown-hook", Boolean.TRUE.toString(), "");
        //PropertyUtils.setProperty("server.port", "8080", "");
        PropertyUtils.setProperty("java.net.preferIPv4Stack", Boolean.TRUE.toString(), "");
        PropertyUtils.setProperty("sun.rmi.dgc.client.gcInterval", "7200000", "");
        PropertyUtils.setProperty("sun.rmi.dgc.server.gcInterval", "7200000", "");
        PropertyUtils.setProperty("java.security.egd", "file:/dev/./urandom", "");
        PropertyUtils.setProperty("file.encoding", "UTF-8", "");
        PropertyUtils.setProperty("management.endpoint.health.show-details", "ALWAYS", "");

        String property = PropertyUtils.getProperty("spring.output.ansi.enabled", "");
        if(StrUtil.isEmpty(property)){
            PropertyUtils.setProperty("spring.output.ansi.enabled", "ALWAYS", "");
        }

    }

    private void createComponent() {

        statusListenerList = SpringFactoriesLoader.loadFactories(StatusListener.class,
                Thread.currentThread().getContextClassLoader());

    }

    private void excludeAutoConfiguration(ConfigurableEnvironment environment) {

    }
}
