package com.kuma.boot.monitor.warn;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.core.utils.servlet.RequestUtils;
import com.kuma.boot.core.utils.RequestWebUtils;
import com.kuma.boot.monitor.autoconfigure.properties.WarnProperties;
import com.kuma.boot.monitor.model.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SmsWarn extends AbstractWarn {
   private static final String CLASS = "com.kuma.boot.sms.service.SmsService";
   private final boolean driverExist = ReflectionUtils.tryClassForName("com.kuma.boot.sms.service.SmsService") != null;

   public SmsWarn() {
   }

   public void notify(Message message) {
      if (!this.driverExist) {
         LogUtils.error("未找到SmsService, 不支持短信预警", new Object[0]);
      } else {
         WarnProperties warnProperties = (WarnProperties)ContextUtils.getBean(WarnProperties.class, true);
         Object smsService = ContextUtils.getBean(ReflectionUtils.tryClassForName("com.kuma.boot.sms.service.SmsService"), true);
         if (Objects.nonNull(warnProperties) && Objects.nonNull(smsService)) {
            String ip = RequestUtils.getIpAddress();
            String dingDingFilterIP = warnProperties.getDingdingFilterIP();
            if (!StringUtils.isEmpty(ip) && !dingDingFilterIP.contains(ip)) {
               String var10000 = StringUtils.subString3(message.getTitle(), 100);
               String context = var10000 + "\n详情: " + RequestWebUtils.getBaseUrl() + "/kuma/cloud/health/\n" + StringUtils.subString3(message.getContent(), 500);

               try {
                  List<Object> param = new ArrayList();
                  param.add("phoneNumber");
                  param.add("signName");
                  param.add("templateCode");
                  param.add(context);
                  ReflectionUtils.callMethod(smsService, "sendSms", param.toArray());
               } catch (Exception e) {
                  LogUtils.error(e);
               }
            }
         }

      }
   }
}
