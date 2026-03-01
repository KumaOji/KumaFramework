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

package com.kuma.cloud.stream.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.stream.properties.RocketmqCustomProperties;
import com.kuma.cloud.stream.properties.StreamProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * StreamAutoConfiguration
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-25 09:41:50
 */
@AutoConfiguration
@EnableConfigurationProperties({RocketmqCustomProperties.class, StreamProperties.class})
//@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:stream.yml")
public class StreamAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(StreamAutoConfiguration.class, StarterNameConstants.STREAM_CLOUD_STARTER);
    }
}
