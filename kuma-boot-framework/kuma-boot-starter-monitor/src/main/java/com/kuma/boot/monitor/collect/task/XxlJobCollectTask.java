package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.handler.annotation.XxlJob;
import java.util.Objects;

public class XxlJobCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.xxljob";
   private final CollectTaskProperties properties;
   private final boolean classExist;

   public XxlJobCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
      this.classExist = ClassUtils.isExist("com.xxl.job.core.executor.impl.XxlJobSpringExecutor");
   }

   public int getTimeSpan() {
      return this.properties.getXxljobTimeSpan();
   }

   public boolean getEnabled() {
      return this.properties.isXxljobEnabled() && this.classExist;
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.xxljob";
   }

   protected CollectInfo getData() {
      try {
         XxlJobSpringExecutor xxlJobSpringExecutor = (XxlJobSpringExecutor)ContextUtils.getBean(XxlJobSpringExecutor.class, true);
         if (Objects.isNull(xxlJobSpringExecutor)) {
            return null;
         } else {
            JobInfo data = new JobInfo();
            data.count = ContextUtils.getApplicationContext().getBeanNamesForAnnotation(XxlJob.class).length;
            Object jobThreadRepository = ReflectionUtils.tryGetFieldValue(xxlJobSpringExecutor, "jobThreadRepository", (Object)null);
            if (Objects.nonNull(jobThreadRepository)) {
               data.jobThreadRepository = (Integer)ReflectionUtils.callMethod(jobThreadRepository, "size", (Object[])null);
            }

            Object jobHandlerRepository = ReflectionUtils.tryGetFieldValue(xxlJobSpringExecutor, "jobHandlerRepository", (Object)null);
            if (Objects.nonNull(jobThreadRepository)) {
               data.jobHandlerRepository = (Integer)ReflectionUtils.callMethod(jobHandlerRepository, "size", (Object[])null);
            }

            Object adminBizList = ReflectionUtils.tryGetFieldValue(xxlJobSpringExecutor, "adminBizList", (Object)null);
            if (Objects.nonNull(adminBizList)) {
               data.adminBizList = (Integer)ReflectionUtils.callMethod(adminBizList, "size", (Object[])null);
            }

            data.logRetentionDays = (Integer)ReflectionUtils.tryGetValue(xxlJobSpringExecutor, "logRetentionDays");
            return data;
         }
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }

         return null;
      }
   }

   private static class JobInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.xxljob.count",
         desc = "xxljob\u4efb\u52a1\u6570\u91cf"
      )
      private Integer count = 0;
      @FieldReport(
         name = "kmc.monitor.collect.xxljob.job.thread.repository",
         desc = "xxljob thread repository\u6570\u91cf"
      )
      private Integer jobThreadRepository = 0;
      @FieldReport(
         name = "kmc.monitor.collect.xxljob.job.handler.repository",
         desc = "xxljob handler repository \u6570\u91cf"
      )
      private Integer jobHandlerRepository = 0;
      @FieldReport(
         name = "kmc.monitor.collect.xxljob.admin.biz.list",
         desc = "xxljob admin biz \u6570\u91cf"
      )
      private Integer adminBizList = 0;
      @FieldReport(
         name = "kmc.monitor.collect.xxljob.log.retention.days",
         desc = "xxljob\u65e5\u5fd7\u4fdd\u7559\u5929\u6570"
      )
      private Integer logRetentionDays = 0;

      private JobInfo() {
      }
   }
}
