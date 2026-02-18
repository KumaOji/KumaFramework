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

package com.kuma.boot.core.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

/**
 * RestTemplate配置
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/8/24 23:48
 */
@AutoConfiguration
public class RestClientAutoConfiguration implements InitializingBean {


    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RestClientAutoConfiguration.class, StarterNameConstants.CORE_STARTER);
    }

    @Bean
    public RestClientCustomizer restClientCustomizer(){
        return new RestClientCustomizer() {
            @Override
            public void customize( RestClient.Builder restClientBuilder ) {
            }
        };
    }


}
