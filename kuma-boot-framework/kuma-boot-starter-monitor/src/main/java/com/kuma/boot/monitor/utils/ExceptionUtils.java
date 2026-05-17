package com.kuma.boot.monitor.utils;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.core.utils.common.PropertyUtils;
import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.core.utils.servlet.RequestUtils;
import com.kuma.boot.common.enums.ExceptionTypeEnum;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.enums.WarnLevelEnum;
import com.kuma.boot.monitor.enums.WarnTypeEnum;
import com.kuma.boot.monitor.model.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ExceptionUtils {
   private static final String exceptionUrl = "kuma.boot.monitor.report.exception.url";

   public ExceptionUtils() {
   }

   public static void reportException(Message message, String applicationName) {
      if (message.getWarnType() == WarnTypeEnum.ERROR) {
         AtomicReference<String> title = new AtomicReference(message.getTitle());
         Monitor monitorThreadPool = (Monitor)ContextUtils.getBean(Monitor.class, false);
         if (Objects.nonNull(monitorThreadPool)) {
            monitorThreadPool.monitorSubmit("系统任务: reportException 异常上报", (Runnable)(() -> {
               Map<String, Object> param = new HashMap();
               param.put("exceptionTitle", title.get());
               param.put("exceptionType", message.getExceptionType().getCode());
               param.put("exceptionLevel", message.getLevelType().getLevel());
               if (StringUtils.isNotBlank(message.getExceptionCode())) {
                  param.put("exceptionCode", message.getExceptionCode());
               }

               if (StringUtils.isNotBlank(message.getBizScope())) {
                  param.put("bizScope", message.getBizScope());
               }

               param.put("exceptionContent", String.format("[%s][%s][%s]%s", RequestUtils.getIpAddress(), PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY), PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY), message.getContent()));
               if (StringUtils.isNotBlank(applicationName)) {
                  param.put("applicationName", applicationName);
               } else {
                  param.put("applicationName", PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY));
               }

            }));
         }
      }

   }

   public static void reportException(Message message) {
      reportException(message, (String)null);
   }

   public static void reportException(WarnLevelEnum warnLevelEnum, String title, String content) {
      reportException(new Message(WarnTypeEnum.ERROR, title, content, warnLevelEnum, ExceptionTypeEnum.BE, (String)null, (String)null), (String)null);
   }

   public static void reportException(WarnLevelEnum warnLevelEnumType, String title, String content, String applicationName) {
      reportException(new Message(WarnTypeEnum.ERROR, title, content, warnLevelEnumType, ExceptionTypeEnum.BE, (String)null, (String)null), applicationName);
   }
}
