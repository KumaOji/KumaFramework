package com.kuma.boot.job.xxl.executor.core;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.xxl.executor.annotation.XxlRegister;
import com.kuma.boot.job.xxl.executor.model.XxlJobGroup;
import com.kuma.boot.job.xxl.executor.model.XxlJobInfo;
import com.kuma.boot.job.xxl.executor.service.JobGroupService;
import com.kuma.boot.job.xxl.executor.service.JobInfoService;
import com.xxl.job.core.handler.annotation.XxlJob;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

@Component
public class XxlJobAutoRegister implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {
   private ApplicationContext applicationContext;
   private final JobGroupService jobGroupService;
   private final JobInfoService jobInfoService;

   public XxlJobAutoRegister(JobGroupService jobGroupService, JobInfoService jobInfoService) {
      this.jobGroupService = jobGroupService;
      this.jobInfoService = jobInfoService;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   public void onApplicationEvent(ApplicationReadyEvent event) {
      try {
         this.addJobGroup();
      } catch (Exception var3) {
         LogUtils.error("get xxl-job cookie error!", new Object[0]);
         return;
      }

      this.addJobInfo();
   }

   private void addJobGroup() {
      if (!this.jobGroupService.preciselyCheck()) {
         if (this.jobGroupService.autoRegisterGroup()) {
            LogUtils.info("auto register xxl-job group success!", new Object[0]);
         }

      }
   }

   private void addJobInfo() {
      List<XxlJobGroup> jobGroups = this.jobGroupService.getJobGroup();
      XxlJobGroup xxlJobGroup = (XxlJobGroup)jobGroups.get(0);
      String[] beanDefinitionNames = this.applicationContext.getBeanNamesForType(Object.class, false, true);

      for(String beanDefinitionName : beanDefinitionNames) {
         Object bean = this.applicationContext.getBean(beanDefinitionName);
         Map<Method, XxlJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(), (method) -> (XxlJob)AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));

         for(Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
            Method executeMethod = (Method)methodXxlJobEntry.getKey();
            XxlJob xxlJob = (XxlJob)methodXxlJobEntry.getValue();
            if (executeMethod.isAnnotationPresent(XxlRegister.class)) {
               XxlRegister xxlRegister = (XxlRegister)executeMethod.getAnnotation(XxlRegister.class);
               List<XxlJobInfo> jobInfo = this.jobInfoService.getJobInfo(xxlJobGroup.getId(), xxlJob.value());
               if (!jobInfo.isEmpty()) {
                  Optional<XxlJobInfo> first = jobInfo.stream().filter((xxlJobInfox) -> xxlJobInfox.getExecutorHandler().equals(xxlJob.value())).findFirst();
                  if (first.isPresent()) {
                     continue;
                  }
               }

               XxlJobInfo xxlJobInfo = this.createXxlJobInfo(xxlJobGroup, xxlJob, xxlRegister);
               Integer jobInfoId = this.jobInfoService.addJobInfo(xxlJobInfo);
               LogUtils.info("xxljob \u81ea\u52a8\u6ce8\u518c\u6210\u529f XxlJobInfo: {}, jobInfoId: {}", new Object[]{xxlJobInfo, jobInfoId});
            }
         }
      }

   }

   private XxlJobInfo createXxlJobInfo(XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlRegister xxlRegister) {
      XxlJobInfo xxlJobInfo = new XxlJobInfo();
      xxlJobInfo.setJobGroup(xxlJobGroup.getId());
      xxlJobInfo.setJobDesc(xxlRegister.jobDesc());
      xxlJobInfo.setAuthor(xxlRegister.author());
      xxlJobInfo.setScheduleType("CRON");
      xxlJobInfo.setScheduleConf(xxlRegister.cron());
      xxlJobInfo.setGlueType("BEAN");
      xxlJobInfo.setExecutorHandler(xxlJob.value());
      xxlJobInfo.setExecutorRouteStrategy(xxlRegister.executorRouteStrategy());
      xxlJobInfo.setMisfireStrategy("DO_NOTHING");
      xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
      xxlJobInfo.setExecutorTimeout(0);
      xxlJobInfo.setExecutorFailRetryCount(0);
      xxlJobInfo.setGlueRemark("GLUE\u4ee3\u7801\u521d\u59cb\u5316");
      xxlJobInfo.setTriggerStatus(xxlRegister.triggerStatus());
      return xxlJobInfo;
   }
}
