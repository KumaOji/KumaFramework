/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.eventbus.disruptor.tmp1.consumer;

import com.lmax.disruptor.EventHandler;
import com.kuma.boot.eventbus.disruptor.tmp1.event.DataEvent;
import com.kuma.boot.eventbus.disruptor.tmp1.event.OrderlyDataEvent;
import com.kuma.boot.eventbus.disruptor.tmp1.thread.OrderlyExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * The type Queue consumer.
 *
 * @param <T> the type parameter
 */
public class QueueConsumer<T> implements EventHandler<DataEvent<T>> {

   private final OrderlyExecutor executor;

   private final QueueConsumerFactory<T> factory;

   /**
    * Instantiates a new Queue consumer.
    *
    * @param executor the executor
    * @param factory the factory
    */
   public QueueConsumer(final OrderlyExecutor executor, final QueueConsumerFactory<T> factory) {
      this.executor = executor;
      this.factory = factory;
   }

   private ThreadPoolExecutor orderly(final DataEvent<T> t) {
      if (t instanceof OrderlyDataEvent && !isEmpty(((OrderlyDataEvent<T>) t).getHash())) {
         return executor.select(((OrderlyDataEvent<T>) t).getHash());
      } else {
         return executor;
      }
   }

   private boolean isEmpty(final String t) {
      return t == null || t.isEmpty();
   }

   @Override
   public void onEvent(DataEvent<T> t, long l, boolean b) throws Exception {
      if (t != null) {
         ThreadPoolExecutor executor = orderly(t);
         QueueConsumerExecutor<T> queueConsumerExecutor = factory.create();
         queueConsumerExecutor.setData(t.getData());
         // help gc
         t.setData(null);
         executor.execute(queueConsumerExecutor);
      }
   }
}
