package com.kuma.boot.test.junitperf.support.i18n;

import com.kuma.boot.test.junitperf.model.vo.I18nVo;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL
)
public class I18N {
   private static final String DEFAULT_PROPERTIES_FILE_NAME = "i18n.JunitPerfMessages";

   public I18N() {
   }

   public static String get(final String key) {
      Locale currentLocale = Locale.getDefault();
      ResourceBundle myResources = ResourceBundle.getBundle("i18n.JunitPerfMessages", currentLocale);
      return myResources.getString(key);
   }

   public static I18nVo buildI18nVo() {
      I18nVo vo = new I18nVo();
      vo.setJunit_performance_report(get("junit_performance_report"));
      vo.setTop(get("top"));
      vo.setReport_created_by(get("report_created_by"));
      vo.setWarm_up(get("warm_up"));
      vo.setStarted_at(get("started_at"));
      vo.setExecution_time(get("execution_time"));
      vo.setInvocations(get("invocations"));
      vo.setThread_count(get("thread_count"));
      vo.setSuccess(get("success"));
      vo.setType(get("type"));
      vo.setActual(get("actual"));
      vo.setRequired(get("required"));
      vo.setThroughput(get("throughput"));
      vo.setMax_latency(get("max_latency"));
      vo.setMin_latency(get("min_latency"));
      vo.setAvg_latency(get("avg_latency"));
      vo.setMemory(get("memory"));
      return vo;
   }

   public static class Key {
      public static final String memory = "memory";
      public static final String warm_up = "warm_up";
      public static final String max_latency = "max_latency";
      public static final String min_latency = "min_latency";
      public static final String thread_count = "thread_count";
      public static final String report_created_by = "report_created_by";
      public static final String started_at = "started_at";
      public static final String required = "required";
      public static final String junit_performance_report = "junit_performance_report";
      public static final String type = "type";
      public static final String top = "top";
      public static final String throughput = "throughput";
      public static final String avg_latency = "avg_latency";
      public static final String invocations = "invocations";
      public static final String execution_time = "execution_time";
      public static final String success = "success";
      public static final String actual = "actual";
      public static final String reportIsEmpty = "reportIsEmpty";

      public Key() {
      }
   }
}
