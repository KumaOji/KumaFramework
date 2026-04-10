package com.kuma.boot.job.xxl.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.xxl.timetask.EveryDayExecute;
import com.kuma.boot.job.xxl.timetask.EveryHourExecute;
import com.kuma.boot.job.xxl.timetask.EveryMinuteExecute;
import com.kuma.boot.job.xxl.timetask.TimedTaskJobHandler;
import com.kuma.boot.lock.autoconfigure.LockAutoConfiguration;
import com.kuma.boot.lock.support.DistributedLock;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
   before = {XxlJobAutoConfiguration.class},
   after = {LockAutoConfiguration.class}
)
public class XxlJobTimeTaskAutoConfiguration implements InitializingBean {
   public XxlJobTimeTaskAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(XxlJobTimeTaskAutoConfiguration.class, "kuma-boot-starter-job-xxl", new String[0]);
   }

   @Bean
   @ConditionalOnBean({DistributedLock.class})
   public TimedTaskJobHandler timedTaskJobHandler(ObjectProvider<List<EveryMinuteExecute>> everyMinuteExecutes, ObjectProvider<List<EveryHourExecute>> everyHourExecutes, ObjectProvider<List<EveryDayExecute>> everyDayExecutes, DistributedLock distributedLock) {
      return new TimedTaskJobHandler((List)everyMinuteExecutes.getIfAvailable(ArrayList::new), (List)everyHourExecutes.getIfAvailable(ArrayList::new), (List)everyDayExecutes.getIfAvailable(ArrayList::new), distributedLock);
   }
}
