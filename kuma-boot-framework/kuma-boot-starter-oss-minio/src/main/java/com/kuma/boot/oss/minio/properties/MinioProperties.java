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

package com.kuma.boot.oss.minio.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * MinioConfiguration
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-13 09:07:11
 */
@RefreshScope
@ConfigurationProperties(prefix = MinioProperties.PREFIX)
public class MinioProperties {

   public static final String PREFIX = "kuma.boot.oss.platform.minio";

   private String accessKey;

   private String secretKey;

   private String url;

   private String bucketName;

   public String getAccessKey() {
      return accessKey;
   }

   public void setAccessKey(String accessKey) {
      this.accessKey = accessKey;
   }

   public String getSecretKey() {
      return secretKey;
   }

   public void setSecretKey(String secretKey) {
      this.secretKey = secretKey;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getBucketName() {
      return bucketName;
   }

   public void setBucketName(String bucketName) {
      this.bucketName = bucketName;
   }
}
