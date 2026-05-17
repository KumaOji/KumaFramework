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

package com.kuma.boot.core.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.support.strategy.BusinessHandler;
import com.kuma.boot.core.support.strategy.BusinessHandlerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 策略模式自动注入配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:13:57
 */
@AutoConfiguration
public class StrategyAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(StrategyAutoConfiguration.class, StarterNameConstants.CORE_STARTER);
    }

    @Bean
    public BusinessHandlerFactory businessHandlerChooser(List<BusinessHandler> businessHandlers) {
        LogUtils.started(BusinessHandlerFactory.class, StarterNameConstants.CORE_STARTER);

        BusinessHandlerFactory businessHandlerChooser = new BusinessHandlerFactory();
        businessHandlerChooser.setBusinessHandlerMap(businessHandlers);
        return businessHandlerChooser;
    }
}
