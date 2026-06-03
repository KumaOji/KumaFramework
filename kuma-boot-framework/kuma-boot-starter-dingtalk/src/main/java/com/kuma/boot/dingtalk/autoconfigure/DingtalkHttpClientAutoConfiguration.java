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
import com.kuma.boot.dingtalk.support.DingerHttpClient;
import com.kuma.boot.dingtalk.support.DingerHttpTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Dinger默认Http客户端配置
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:16:45
 */
@AutoConfiguration
public class DingtalkHttpClientAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingtalkHttpClientAutoConfiguration.class, StarterNameConstants.DINGTALK_STARTER);
    }

    @Bean(name = "dingerClientHttpRequestFactory")
    public ClientHttpRequestFactory dingerClientHttpRequestFactory( DingtalkProperties dingtalkProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout((int) dingtalkProperties.getHttpclient().getReadTimeout().toMillis());
        factory.setConnectTimeout((int) dingtalkProperties.getHttpclient().getConnectTimeout().toMillis());
        return factory;
    }

    @Bean(name = DingerConstant.DINGER_REST_TEMPLATE)
    public RestTemplate dingerRestTemplate(ClientHttpRequestFactory dingerClientHttpRequestFactory) {
        return new RestTemplate(dingerClientHttpRequestFactory);
    }

    @Bean
    public DingerHttpClient dingerHttpClient(
            @Autowired @Qualifier(DingerConstant.DINGER_REST_TEMPLATE) RestTemplate restTemplate) {
        return new DingerHttpTemplate(restTemplate);
    }
}
