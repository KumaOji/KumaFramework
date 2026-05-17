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

package com.kuma.boot.core.startup;

import com.kuma.boot.common.startup.BaseStat;
import com.kuma.boot.common.support.version.KmcVersion;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.core.utils.CoreUtils;
import com.kuma.boot.common.version.SpringCloudAlibabaVersion;
import com.kuma.boot.common.version.SpringCloudDependenciesVersion;
import com.kuma.boot.common.version.SpringCloudVersion;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.kuma.boot.common.constant.CommonConstants.ACTIVE_PROFILES_ACTIVE;
import static com.kuma.boot.common.constant.CommonConstants.ACTIVE_PROFILES_PROPERTY;
import static com.kuma.boot.common.constant.CommonConstants.ENV;

/**
 * Extend of {@link SpringApplication} to compute {@link ApplicationContextInitializer} initialize
 * cost time.
 *
 * @author huzijie
 * @version StartupSpringApplication.java, v 0.1 2022年03月14日 2:27 PM huzijie Exp $
 */
public class StartupSpringApplication extends SpringApplication {

    public static final String DEFAULT_BANNER_LOCATION = "classpath:banner/kmc-banner.txt";

    private final List<BaseStat> initializerStartupStatList = new ArrayList<>();

    public StartupSpringApplication(Class<?>... primarySources) {
        super(primarySources);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void applyInitializers(ConfigurableApplicationContext context) {
        for (ApplicationContextInitializer initializer : getInitializers()) {
            Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(
                    initializer.getClass(), ApplicationContextInitializer.class);
            if (requiredType != null) {
                Assert.isInstanceOf(requiredType, context, "Unable to call initializer.");
                BaseStat stat = new BaseStat();
                stat.setName(initializer.getClass().getName());
                stat.setStartTime(System.currentTimeMillis());
                initializer.initialize(context);
                stat.setEndTime(System.currentTimeMillis());
                initializerStartupStatList.add(stat);
            }
        }
    }

    public List<BaseStat> getInitializerStartupStatList() {
        return initializerStartupStatList;
    }

    /**
     * 注册 KMC Banner：将版本和 URL 信息写入 System Properties（Spring Environment 会读取），
     * 再通过 spring.banner.location 指向 banner 文件，让标准 ResourceBanner 正常解析 ${xxx} 占位符。
     */
    public StartupSpringApplication setKmcBanner() {
        setVersionSystemProperties();
        System.setProperty("spring.banner.location", DEFAULT_BANNER_LOCATION);
        return this;
    }

    public StartupSpringApplication setKmcProfileIfNotExists(String profile) {
        if (StrUtil.isBlank(System.getProperty(ACTIVE_PROFILES_PROPERTY)) && StrUtil.isBlank(
                System.getProperty(ENV)) && !System.getenv().containsKey(ACTIVE_PROFILES_ACTIVE)) {
            System.setProperty(ACTIVE_PROFILES_PROPERTY, profile);
            // 仅在没有激活 profile 时才补充 additional profile，避免与外部传入的 profile 叠加
            super.setAdditionalProfiles(profile);
        }
        return this;
    }

    public StartupSpringApplication setKmcApplicationProperty(String applicationName) {
        // 将 spring.application.name 写入 System 属性（优先级高于 application.yml），
        // 保证 event.getApplicationContext().getEnvironment().getProperty("spring.application.name") 可用。
        // 注意：ApplicationContext.getApplicationName() 在 Spring Boot 中始终返回 ""，
        //       请勿使用该 API 获取应用名，应使用 BootContextUtils.getApplicationName(context) 代替。
        if (StrUtil.isNotBlank(applicationName) && System.getProperty("spring.application.name") == null) {
            System.setProperty("spring.application.name", applicationName);
        }
        PropertyUtils.setDefaultProperty(applicationName);
        return this;
    }

    public StartupSpringApplication setKmcAllowBeanDefinitionOverriding(
            boolean allowBeanDefinitionOverriding) {
        super.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
        return this;
    }

    @Override
    protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        super.configureEnvironment(environment, args);
    }

    @Override
    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {

    }

    // ── private ──────────────────────────────────────────────────────────────

    private static void setVersionSystemProperties() {
        putIfNotEmpty("spring.version", SpringVersion.getVersion());
        putIfNotEmpty("kmc-boot.version", KmcVersion.getVersion());
        putIfNotEmpty("spring-cloud.version", SpringCloudVersion.getVersion());
        putIfNotEmpty("spring-cloud-dependencies.version", SpringCloudDependenciesVersion.getVersion());
        putIfNotEmpty("spring-cloud-alibaba.version", SpringCloudAlibabaVersion.getVersion());
        CoreUtils.getUrlMap().forEach((k, v) -> System.setProperty(k, String.valueOf(v)));
    }

    private static void putIfNotEmpty(String key, String value) {
        if (value != null && !value.isEmpty()) {
            System.setProperty(key, value);
        }
    }
}
