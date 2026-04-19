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

package com.kuma.boot.data.jpa.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContextEvent;
import org.springframework.data.repository.init.RepositoriesPopulatedEvent;

/**
 * DataEventListenerAutoConfiguration
 *
 * @author kuma
 * @version 2026.03
 * @since 2025-12-19 09:30:45
 */
@AutoConfiguration
public class DataEventListenerAutoConfiguration {

   @Configuration
   public static class RepositoriesPopulatedEventListener
           implements ApplicationListener<RepositoriesPopulatedEvent> {

      @Override
      public void onApplicationEvent( RepositoriesPopulatedEvent event ) {
         LogUtils.info(
                 "DataEventListener ----- RepositoriesPopulatedEvent onApplicationEvent {}",
                 event);
      }
   }

   @Configuration
   public static class MappingContextEventListener
           implements ApplicationListener<MappingContextEvent> {

      @Override
      public void onApplicationEvent( MappingContextEvent event ) {
         LogUtils.info(
                 "DataEventListener ----- MappingContextEvent onApplicationEvent {}", event);
      }
   }
}
