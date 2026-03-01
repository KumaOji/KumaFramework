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

package com.kuma.cloud.sentinel.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sentinel.autoconfigure.SentinelAutoConfiguration;
import com.kuma.cloud.sentinel.properties.SentinelCloudProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * SentinelAutoConfiguration
 *
 * <pre>{@code
 *        // 注解方式进行埋点，注解方式受 AOP 代理的诸多限制
 *   @SentinelResource("com.alibabacloud.mse.demo.AApplication.AController:a")
 *   private String a(HttpServletRequest request) {
 *       StringBuilder headerSb = new StringBuilder();
 *       Enumeration<String> enumeration = request.getHeaderNames();
 *       while (enumeration.hasMoreElements()) {
 *           String headerName = enumeration.nextElement();
 *           Enumeration<String> val = request.getHeaders(headerName);
 *           while (val.hasMoreElements()) {
 *               String headerVal = val.nextElement();
 *               headerSb.append(headerName + ":" + headerVal + ",");
 *           }
 *       }
 *       return "A"+SERVICE_TAG+"[" + inetUtils.findFirstNonLoopbackAddress().getHostAddress() + "]" + " -> " +
 *               restTemplate.getForObject("http://sc-B/b", String.class);
 *   }
 *
 *   // SDK 方式增加流控降级能力，需要侵入业务代码
 *   private String helloWorld(HttpServletRequest request) {
 *       Entry entry = null;
 *       try {
 *           entry = SphU.entry("HelloWorld");
 *           StringBuilder headerSb = new StringBuilder();
 *           Enumeration<String> enumeration = request.getHeaderNames();
 *           while (enumeration.hasMoreElements()) {
 *               String headerName = enumeration.nextElement();
 *               Enumeration<String> val = request.getHeaders(headerName);
 *               while (val.hasMoreElements()) {
 *                   String headerVal = val.nextElement();
 *                   headerSb.append(headerName + ":" + headerVal + ",");
 *               }
 *           }
 *           return "A"+SERVICE_TAG+"[" + inetUtils.findFirstNonLoopbackAddress().getHostAddress() + "]" + " -> " +
 *                   restTemplate.getForObject("http://sc-B/b", String.class);
 *       } catch (BlockException ex) {
 *         System.err.println("blocked!");
 *       } finally {
 *           if (entry != null) {
 *               entry.exit();
 *           }
 *       }
 *   }
 *     }
 * </pre>
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 20:54:47
 */
@AutoConfiguration(after = SentinelAutoConfiguration.class)
@EnableConfigurationProperties({SentinelCloudProperties.class})
@ConditionalOnProperty(
        prefix = SentinelCloudProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
public class SentinelCloudAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(SentinelCloudAutoConfiguration.class, StarterNameConstants.SENTINEL_CLOUD_STARTER);
    }

}
