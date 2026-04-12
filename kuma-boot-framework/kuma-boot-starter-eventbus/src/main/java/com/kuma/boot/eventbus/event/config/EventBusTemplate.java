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

package com.kuma.boot.eventbus.event.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.eventbus.event.annotation.MessageEventBus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于google的eventBus总线执行器
 */
@Component
public class EventBusTemplate {

   /**
    * 同步事件处理器
    */
   private final EventBus syncEventBus;

   /**
    * 异步事件处理器
    */
   private final AsyncEventBus asyncEventBus;

   public EventBusTemplate(EventBus syncEventBus, AsyncEventBus asyncEventBus) {
      this.syncEventBus = syncEventBus;
      this.asyncEventBus = asyncEventBus;
   }

   /**
    * 发送异步通知
    *
    * @param event
    */
   public void postSync(Object event) {
      syncEventBus.post(event);
   }

   /**
    * 发送同步通知
    *
    * @param event
    */
   public void postAsync(Object event) {
      asyncEventBus.post(event);
   }

   /**
    * 初始化监听器注册
    */
   @PostConstruct
   public void initialize() {
      List<Object> listeners = ContextUtils.getBeansWithAnnotationList(MessageEventBus.class);
      for (Object listener : listeners) {
         asyncEventBus.register(listener);
         syncEventBus.register(listener);
      }
   }
}
