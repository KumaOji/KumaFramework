package com.kuma.boot.job.xxl.timetask;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.lock.support.DistributedLock;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.tool.response.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimedTaskJobHandler {
   private final List<EveryMinuteExecute> everyMinuteExecutes;
   private final List<EveryHourExecute> everyHourExecutes;
   private final List<EveryDayExecute> everyDayExecutes;
   private final DistributedLock distributedLock;

   public TimedTaskJobHandler(List<EveryMinuteExecute> everyMinuteExecutes, List<EveryHourExecute> everyHourExecutes, List<EveryDayExecute> everyDayExecutes, DistributedLock distributedLock) {
      this.everyMinuteExecutes = everyMinuteExecutes;
      this.everyHourExecutes = everyHourExecutes;
      this.everyDayExecutes = everyDayExecutes;
      this.distributedLock = distributedLock;
   }

   @XxlJob("everyMinuteExecute")
   public Response<String> everyMinuteExecute(String param) {
      LogUtils.info("\u6bcf\u5206\u949f\u4efb\u52a1\u6267\u884c", new Object[0]);
      if (this.everyMinuteExecutes != null && !this.everyMinuteExecutes.isEmpty()) {
         for(EveryMinuteExecute everyMinuteExecute : this.everyMinuteExecutes) {
            Response var5;
            try {
               boolean result = this.distributedLock.tryLock("timetask-everyMinuteExecute-" + everyMinuteExecute.getClass().getName(), 30L, TimeUnit.SECONDS);
               if (result) {
                  everyMinuteExecute.execute();
               }
               continue;
            } catch (Exception e) {
               LogUtils.error("\u6bcf\u5206\u949f\u4efb\u52a1\u5f02\u5e38", new Object[]{e});
               var5 = Response.ofFail();
            } finally {
               this.distributedLock.unlock();
            }

            return var5;
         }

         return Response.ofSuccess();
      } else {
         return Response.ofSuccess();
      }
   }

   @XxlJob("everyHourExecuteJobHandler")
   public Response<String> everyHourExecuteJobHandler(String param) {
      LogUtils.info("\u6bcf\u5c0f\u65f6\u4efb\u52a1\u6267\u884c", new Object[0]);
      if (this.everyHourExecutes != null && this.everyHourExecutes.size() != 0) {
         for(EveryHourExecute everyHourExecute : this.everyHourExecutes) {
            Response var5;
            try {
               boolean result = this.distributedLock.tryLock("timetask-everyHourExecute-" + everyHourExecute.getClass().getName(), 30L, TimeUnit.SECONDS);
               if (result) {
                  everyHourExecute.execute();
               }
               continue;
            } catch (Exception e) {
               LogUtils.error("\u6bcf\u5c0f\u65f6\u4efb\u52a1\u5f02\u5e38", new Object[]{e});
               var5 = Response.ofFail();
            } finally {
               this.distributedLock.unlock();
            }

            return var5;
         }

         return Response.ofSuccess();
      } else {
         return Response.ofSuccess();
      }
   }

   @XxlJob("everyDayExecuteJobHandler")
   public Response<String> everyDayExecuteJobHandler(String param) {
      LogUtils.info("\u6bcf\u65e5\u4efb\u52a1\u6267\u884c", new Object[0]);
      if (this.everyDayExecutes != null && this.everyDayExecutes.size() != 0) {
         for(EveryDayExecute everyDayExecute : this.everyDayExecutes) {
            Response var5;
            try {
               boolean result = this.distributedLock.tryLock("timetask-everyDayExecute-" + everyDayExecute.getClass().getName(), 30L, TimeUnit.SECONDS);
               if (result) {
                  everyDayExecute.execute();
               }
               continue;
            } catch (Exception e) {
               LogUtils.error("\u6bcf\u5929\u4efb\u52a1\u5f02\u5e38", new Object[]{e});
               var5 = Response.ofFail();
            } finally {
               this.distributedLock.unlock();
            }

            return var5;
         }

         return Response.ofSuccess();
      } else {
         return Response.ofSuccess();
      }
   }
}
