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

package com.kuma.boot.core.startup;

import com.kuma.boot.common.enums.EventEnum;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.core.banner.KmcBanner;
import java.util.Collections;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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

    public static final String DEFAULT_BANNER_LOCATION = "kmc-banner.txt";

    private final List<com.kuma.boot.core.startup.BaseStat> initializerStartupStatList = new ArrayList<>();

    private boolean isKmcBanner = false;

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
                com.kuma.boot.core.startup.BaseStat stat = new com.kuma.boot.core.startup.BaseStat();
                stat.setName(initializer.getClass().getName());
                stat.setStartTime(System.currentTimeMillis());
                initializer.initialize(context);
                stat.setEndTime(System.currentTimeMillis());
                initializerStartupStatList.add(stat);
            }
        }
    }

    public List<com.kuma.boot.core.startup.BaseStat> getInitializerStartupStatList() {
        return initializerStartupStatList;
    }

    public StartupSpringApplication setKmcBanner() {
        final ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(DEFAULT_BANNER_LOCATION);
        super.setBanner(new KmcBanner(resource));
        System.setProperty("spring.banner.location", DEFAULT_BANNER_LOCATION);
        isKmcBanner = true;
        return this;
    }

    public StartupSpringApplication setKmcProfileIfNotExists(String profile) {
        if (StrUtil.isBlank(System.getProperty(ACTIVE_PROFILES_PROPERTY)) && StrUtil.isBlank(
                System.getProperty(ENV)) && !System.getenv().containsKey(ACTIVE_PROFILES_ACTIVE)) {
            System.setProperty(ACTIVE_PROFILES_PROPERTY, profile);
        }
        super.setAdditionalProfiles(profile);
        return this;
    }

    public StartupSpringApplication setKmcApplicationProperty(String applicationName) {
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
//		if(isKmcBanner){
//			environment.getPropertySources().addFirst(
//				new MapPropertySource("kmcPropertySource",
//					Collections.singletonMap("spring.banner.location", "xxxxxxx.txt"))
//			);
//		}
    }

    @Override
    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {

    }
}
