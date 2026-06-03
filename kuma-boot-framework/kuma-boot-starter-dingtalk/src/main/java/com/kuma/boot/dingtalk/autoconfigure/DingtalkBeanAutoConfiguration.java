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
import com.kuma.boot.dingtalk.multi.MultiDingerAlgorithmInjectRegister;
import com.kuma.boot.dingtalk.support.CustomMessage;
import com.kuma.boot.dingtalk.support.DefaultDingerAsyncCallable;
import com.kuma.boot.dingtalk.support.DefaultDingerExceptionCallback;
import com.kuma.boot.dingtalk.support.DefaultDingerIdGenerator;
import com.kuma.boot.dingtalk.support.DingTalkSignAlgorithm;
import com.kuma.boot.dingtalk.support.DingerAsyncCallback;
import com.kuma.boot.dingtalk.support.DingerExceptionCallback;
import com.kuma.boot.dingtalk.support.DingerIdGenerator;
import com.kuma.boot.dingtalk.support.MarkDownMessage;
import com.kuma.boot.dingtalk.support.TextMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import static com.kuma.boot.dingtalk.constant.DingerConstant.MARKDOWN_MESSAGE;
import static com.kuma.boot.dingtalk.constant.DingerConstant.TEXT_MESSAGE;

/**
 * 实例化bean配置
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:16:53
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = DingtalkProperties.PREFIX, value = "enabled", havingValue = "true")
public class DingtalkBeanAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingtalkBeanAutoConfiguration.class, StarterNameConstants.DINGTALK_STARTER);
    }

    /**
     * 默认Text消息格式配置
     */
    @Bean(name = TEXT_MESSAGE)
    @ConditionalOnMissingBean(name = TEXT_MESSAGE)
    public CustomMessage textMessage() {
        return new TextMessage();
    }

    /**
     * 默认markdown消息格式配置
     */
    @Bean(name = MARKDOWN_MESSAGE)
    @ConditionalOnMissingBean(name = MARKDOWN_MESSAGE)
    public CustomMessage markDownMessage() {
        return new MarkDownMessage();
    }

    /**
     * 默认签名算法
     */
    @Bean
    public DingTalkSignAlgorithm dingerSignAlgorithm() {
        return new DingTalkSignAlgorithm();
    }

    /**
     * 默认dkid生成配置
     */
    @Bean
    public DingerIdGenerator dingerIdGenerator() {
        return new DefaultDingerIdGenerator();
    }

    /**
     * 默认异步执行回调实例
     */
    @Bean
    public DingerAsyncCallback dingerAsyncCallback() {
        return new DefaultDingerAsyncCallable();
    }

    @Bean
    public DingerExceptionCallback dingerExceptionCallback() {
        return new DefaultDingerExceptionCallback();
    }

    @Bean
    public MultiDingerAlgorithmInjectRegister multiDingerAlgorithmInjectRegister() {
        return new MultiDingerAlgorithmInjectRegister();
    }
}
