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

public class MailWarn extends AbstractWarn {
   private static final String CLASS = "com.kuma.boot.mail.template.MailTemplate";
   private final boolean driverExist = ReflectionUtils.tryClassForName("com.kuma.boot.mail.template.MailTemplate") != null;

   public MailWarn() {
   }

   public void notify(Message message) {
      if (!this.driverExist) {
         LogUtils.error("未找到MailTemplate, 不支持邮件预警", new Object[0]);
      } else {
         WarnProperties warnProperties = (WarnProperties)ContextUtils.getBean(WarnProperties.class, true);
         Object mailTemplate = ContextUtils.getBean(ReflectionUtils.tryClassForName("com.kuma.boot.mail.template.MailTemplate"), true);
         if (Objects.nonNull(mailTemplate) && Objects.nonNull(warnProperties)) {
            String ip = RequestUtils.getIpAddress();
            String dingDingFilterIP = warnProperties.getDingdingFilterIP();
            if (!StringUtils.isEmpty(ip) && !dingDingFilterIP.contains(ip)) {
               String var10000 = StringUtils.subString3(message.getTitle(), 100);
               String context = var10000 + "\n详情: " + RequestWebUtils.getBaseUrl() + "/health/\n" + StringUtils.subString3(message.getContent(), 500);

               try {
                  List<Object> param = new ArrayList();
                  param.add("981376577@qq.com");
                  param.add("服务状态预警");
                  param.add(context);
                  ReflectionUtils.callMethod(mailTemplate, "sendSimpleMail", param.toArray());
               } catch (Exception e) {
                  LogUtils.error(e);
               }
            }
         }

      }
   }
}
