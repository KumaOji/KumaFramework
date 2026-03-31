package com.kuma.boot.monitor.warn;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
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
         LogUtils.error("\u672a\u627e\u5230SmsService, \u4e0d\u652f\u6301\u77ed\u4fe1\u9884\u8b66", new Object[0]);
      } else {
         WarnProperties warnProperties = (WarnProperties)ContextUtils.getBean(WarnProperties.class, true);
         Object smsService = ContextUtils.getBean(ReflectionUtils.tryClassForName("com.kuma.boot.sms.service.SmsService"), true);
         if (Objects.nonNull(warnProperties) && Objects.nonNull(smsService)) {
            String ip = RequestUtils.getIpAddress();
            String dingDingFilterIP = warnProperties.getDingdingFilterIP();
            if (!StringUtils.isEmpty(ip) && !dingDingFilterIP.contains(ip)) {
               String var10000 = StringUtils.subString3(message.getTitle(), 100);
               String context = var10000 + "\n\u8be6\u60c5: " + RequestWebUtils.getBaseUrl() + "/kuma/cloud/health/\n" + StringUtils.subString3(message.getContent(), 500);

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
