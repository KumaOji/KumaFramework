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

package com.kuma.boot.dingtalk.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.constant.DingerConstant;
import com.kuma.boot.dingtalk.exception.ConfigurationException;
import com.kuma.boot.dingtalk.model.DingerConfigurerAdapter;
import com.kuma.boot.dingtalk.model.DingerManagerBuilder;
import com.kuma.boot.dingtalk.model.DingerRobot;
import com.kuma.boot.dingtalk.model.DingerSender;
import com.kuma.boot.dingtalk.session.DingerSessionFactory;
import com.kuma.boot.dingtalk.session.SessionConfiguration;
import com.kuma.boot.dingtalk.spring.DingerSessionFactoryBean;
import com.kuma.boot.dingtalk.support.CustomMessage;
import com.kuma.boot.dingtalk.support.DingTalkSignAlgorithm;
import com.kuma.boot.dingtalk.support.DingerAsyncCallback;
import com.kuma.boot.dingtalk.support.DingerExceptionCallback;
import com.kuma.boot.dingtalk.support.DingerHttpClient;
import com.kuma.boot.dingtalk.support.DingerIdGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

/**
 * DingerAutoConfiguration
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:16:49
 */
@AutoConfiguration(
        after = {DingtalkBeanAutoConfiguration.class, DingtalkHttpClientAutoConfiguration.class, DingtalkThreadPoolAutoConfiguration.class})
@Import(value = {DingtalkBeanAutoConfiguration.class, DingtalkHttpClientAutoConfiguration.class, DingtalkThreadPoolAutoConfiguration.class})
@EnableConfigurationProperties({DingtalkProperties.class})
@ConditionalOnProperty(prefix = DingtalkProperties.PREFIX, value = "enabled", havingValue = "true")
public class DingtalkAutoConfiguration implements InitializingBean {

    private final DingtalkProperties properties;
    private final ResourceLoader resourceLoader;

    public DingtalkAutoConfiguration(DingtalkProperties dingtalkProperties, ResourceLoader resourceLoader) {
        this.properties = dingtalkProperties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingtalkAutoConfiguration.class, StarterNameConstants.DINGTALK_STARTER);

        checkConfigFileExists();
    }

    @Bean
    @ConditionalOnMissingBean(DingerConfigurerAdapter.class)
    public DingerConfigurerAdapter dingerConfigurerAdapter() {
        return new DingerConfigurerAdapter();
    }

    @Bean
    public DingerManagerBuilder dingerManagerBuilder(
            @Autowired @Qualifier(DingerConstant.DINGER_REST_TEMPLATE) RestTemplate restTemplate,
            DingerExceptionCallback dingerExceptionCallback,
            @Autowired @Qualifier(DingerConstant.TEXT_MESSAGE) CustomMessage textMessage,
            @Autowired @Qualifier(DingerConstant.MARKDOWN_MESSAGE) CustomMessage markDownMessage,
            DingTalkSignAlgorithm dingerSignAlgorithm,
            DingerIdGenerator dingerIdGenerator,
            @Autowired @Qualifier(DingerConstant.DINGER_EXECUTOR) Executor dingTalkExecutor,
            DingerAsyncCallback dingerAsyncCallback,
            DingerHttpClient dingerHttpClient) {

        return new DingerManagerBuilder(
                restTemplate,
                dingerExceptionCallback,
                textMessage,
                markDownMessage,
                dingerSignAlgorithm,
                dingerIdGenerator,
                dingTalkExecutor,
                dingerAsyncCallback,
                dingerHttpClient);
    }

    @Bean
    public DingerSender dingerSender(
            DingerConfigurerAdapter dingerConfigurerAdapter, DingerManagerBuilder dingerManagerBuilder) {
        try {
            dingerConfigurerAdapter.configure(dingerManagerBuilder);
        } catch (Exception ex) {
            throw new ConfigurationException(ex);
        }
        return new DingerRobot(properties, dingerManagerBuilder);
    }

    @Bean
    @ConditionalOnMissingBean
    public DingerSessionFactory dingerSessionFactory(DingerRobot dingerRobot) throws Exception {
        DingerSessionFactoryBean factory = new DingerSessionFactoryBean();
        factory.setConfiguration(SessionConfiguration.of(properties, dingerRobot));
        return factory.getObject();
    }

    private void checkConfigFileExists() {
        if (StringUtils.hasText(this.properties.getDingerLocations())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getDingerLocations());

            Assert.state(
                    resource.exists(),
                    "Cannot find config location: "
                            + resource
                            + " (please add config file or check your Dinger configuration)");
        }
    }
}
