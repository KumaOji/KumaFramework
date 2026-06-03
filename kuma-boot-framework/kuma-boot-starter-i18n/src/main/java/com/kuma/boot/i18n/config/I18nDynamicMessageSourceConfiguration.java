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

package com.kuma.boot.i18n.config;

import com.kuma.boot.i18n.message.DynamicMessageSource;
import com.kuma.boot.i18n.message.I18nMessageProvider;
import com.kuma.boot.i18n.message.MessageSourceHierarchicalChanger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 动态消息源自动配置（可选）.
 *
 * <p>仅当业务应用注册了 {@link I18nMessageProvider} Bean（从 DB / Redis 动态读取翻译）时激活。
 * 将 {@link DynamicMessageSource} 挂入 {@code MessageSource} 层级链，使动态翻译优先于
 * properties 文件查询。
 *
 * @author kuma
 */
@AutoConfiguration(after = I18nMessageSourceAutoConfiguration.class)
public class I18nDynamicMessageSourceConfiguration {

    @Bean(name = DynamicMessageSource.DYNAMIC_MESSAGE_SOURCE_BEAN_NAME)
    @ConditionalOnBean(I18nMessageProvider.class)
    @ConditionalOnMissingBean(name = DynamicMessageSource.DYNAMIC_MESSAGE_SOURCE_BEAN_NAME)
    public DynamicMessageSource dynamicMessageSource(I18nMessageProvider provider) {
        return new DynamicMessageSource(provider);
    }

    /**
     * 当 {@link DynamicMessageSource} 和 {@code messageSource} 均存在时，
     * 调整层级使动态源作为父级，减少无效查询开销.
     */
    @Bean
    @ConditionalOnBean(
            name = {
                AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME,
                DynamicMessageSource.DYNAMIC_MESSAGE_SOURCE_BEAN_NAME
            })
    public MessageSourceHierarchicalChanger messageSourceHierarchicalChanger() {
        return new MessageSourceHierarchicalChanger();
    }
}
