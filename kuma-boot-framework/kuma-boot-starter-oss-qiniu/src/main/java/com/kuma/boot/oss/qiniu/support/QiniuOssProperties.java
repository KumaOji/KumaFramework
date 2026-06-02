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

package com.kuma.boot.oss.qiniu.support;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 七牛云 属性
 *
 * @author kuma
 */
@RefreshScope
@ConfigurationProperties(QiniuOssProperties.PREFIX)
public class QiniuOssProperties extends QiniuOssConfig implements InitializingBean {

   public static final String PREFIX = "kuma.boot.oss.platform.qiniu";

   private Map<String, QiniuOssConfig> ossConfig = new HashMap<>();

   @Override
   public void afterPropertiesSet() {
      if (ossConfig.isEmpty()) {
         this.init();
      } else {
         ossConfig.values().forEach(QiniuOssConfig::init);
      }
   }

   public Map<String, QiniuOssConfig> getOssConfig() {
      return ossConfig;
   }

   public void setOssConfig(Map<String, QiniuOssConfig> ossConfig) {
      this.ossConfig = ossConfig;
   }
}
