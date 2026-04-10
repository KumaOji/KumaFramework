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

package com.kuma.boot.job.xxl.timetask;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.lock.support.DistributedLock;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时器任务
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-04 09:15:24
 */
public class TimedTaskJobHandler {

   private final List<EveryMinuteExecute> everyMinuteExecutes;

   private final List<EveryHourExecute> everyHourExecutes;

   private final List<EveryDayExecute> everyDayExecutes;

   private final DistributedLock distributedLock;

   public TimedTaskJobHandler(
           List<EveryMinuteExecute> everyMinuteExecutes,
           List<EveryHourExecute> everyHourExecutes,
           List<EveryDayExecute> everyDayExecutes,
           DistributedLock distributedLock) {
      this.everyMinuteExecutes = everyMinuteExecutes;
      this.everyHourExecutes = everyHourExecutes;
      this.everyDayExecutes = everyDayExecutes;
      this.distributedLock = distributedLock;
   }

   /**
    * 每分钟任务
    */
   @XxlJob("everyMinuteExecute")
   public ReturnT<String> everyMinuteExecute(String param) {
      LogUtils.info("每分钟任务执行");
      if (everyMinuteExecutes == null || everyMinuteExecutes.isEmpty()) {
         return ReturnT.SUCCESS;
      }

      for (EveryMinuteExecute everyMinuteExecute : everyMinuteExecutes) {
         try {
            boolean result = distributedLock.tryLock(
                    "timetask-everyMinuteExecute-"
                            + everyMinuteExecute.getClass().getName(),
                    30,
                    TimeUnit.SECONDS);
            if (result) {
               everyMinuteExecute.execute();
            }
         }
         catch (Exception e) {
            LogUtils.error("每分钟任务异常", e);
            return ReturnT.FAIL;
         }
         finally {
            distributedLock.unlock();
         }
      }
      return ReturnT.SUCCESS;
   }

   /**
    * 每小时任务
    */
   @XxlJob("everyHourExecuteJobHandler")
   public ReturnT<String> everyHourExecuteJobHandler(String param) {
      LogUtils.info("每小时任务执行");
      if (everyHourExecutes == null || everyHourExecutes.isEmpty()) {
         return ReturnT.SUCCESS;
      }

      for (EveryHourExecute everyHourExecute : everyHourExecutes) {
         try {
            boolean result = distributedLock.tryLock(
                    "timetask-everyHourExecute-"
                            + everyHourExecute.getClass().getName(),
                    30,
                    TimeUnit.SECONDS);
            if (result) {
               everyHourExecute.execute();
            }
         }
         catch (Exception e) {
            LogUtils.error("每小时任务异常", e);
            return ReturnT.FAIL;
         }
         finally {
            distributedLock.unlock();
         }
      }
      return ReturnT.SUCCESS;
   }

   /**
    * 每日任务
    */
   @XxlJob("everyDayExecuteJobHandler")
   public ReturnT<String> everyDayExecuteJobHandler(String param) {
      LogUtils.info("每日任务执行");
      if (everyDayExecutes == null || everyDayExecutes.isEmpty()) {
         return ReturnT.SUCCESS;
      }

      for (EveryDayExecute everyDayExecute : everyDayExecutes) {
         try {
            boolean result = distributedLock.tryLock(
                    "timetask-everyDayExecute-" + everyDayExecute.getClass().getName(), 30,
                    TimeUnit.SECONDS);
            if (result) {
               everyDayExecute.execute();
            }
         }
         catch (Exception e) {
            LogUtils.error("每天任务异常", e);
            return ReturnT.FAIL;
         }
         finally {
            distributedLock.unlock();
         }
      }
      return ReturnT.SUCCESS;
   }
}
