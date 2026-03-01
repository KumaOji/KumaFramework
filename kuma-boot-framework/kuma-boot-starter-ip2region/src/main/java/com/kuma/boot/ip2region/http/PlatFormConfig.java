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

package com.kuma.boot.ip2region.http;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * # 第三方开放平台的服务
 * platform:
 * # 目前支持百度[baidu] 高德[gaode]
 * mode: # 您选择的服务
 * # 相关配置
 * conf:
 * # 百度
 * baidu:
 * ak: # 您的百度地图开放平台服务密钥（AK）
 * # 高德
 * gaode:
 * key: # 您的高德地图开放平台key
 */
@Configuration
public class PlatFormConfig {

    @Bean
    @ConditionalOnProperty(prefix = "platform", name = "mode", havingValue = "baidu")
    public com.kuma.boot.ip2region.http.IPlatFormIpAnalyzeService initBaiduIpAnalyzeService() {
        return new com.kuma.boot.ip2region.http.PlatformBaiduService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "platform", name = "mode", havingValue = "gaode")
    public com.kuma.boot.ip2region.http.IPlatFormIpAnalyzeService initGaodeIpAnalyzeService() {
        return new PlatformGaodeService();
    }
}
