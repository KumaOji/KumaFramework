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

public class DingdingWarn extends AbstractWarn {
   private static final String CLASS = "com.kuma.boot.dingtalk.model.DingerRobot";
   private static final String MESSAGE_SUB_TYPE = "com.kuma.boot.dingtalk.enums.MessageSubType";
   private static final String DINGER_REQUEST = "com.kuma.boot.dingtalk.entity.DingerRequest";
   private final boolean driverExist = ReflectionUtils.tryClassForName("com.kuma.boot.dingtalk.model.DingerRobot") != null;

   public DingdingWarn() {
   }

   public void notify(Message message) {
      if (!this.driverExist) {
         LogUtils.error("\u672a\u627e\u5230DingerRobot, \u4e0d\u652f\u6301\u9489\u9489\u9884\u8b66", new Object[0]);
      } else {
         WarnProperties warnProperties = (WarnProperties)ContextUtils.getBean(WarnProperties.class, true);
         Object dingerRobot = ContextUtils.getBean(ReflectionUtils.tryClassForName("com.kuma.boot.dingtalk.model.DingerRobot"), true);
         if (dingerRobot != null) {
            String ip = RequestUtils.getIpAddress();
            String dingDingFilterIP = warnProperties.getDingdingFilterIP();
            if (!StringUtils.isEmpty(ip) && !dingDingFilterIP.contains(ip)) {
               String var10000 = message.getWarnType().getDescription();
               String title = "[" + var10000 + "]" + StringUtils.subString3(message.getTitle(), 100);
               var10000 = StringUtils.subString3(message.getTitle(), 100);
               String context = var10000 + "\n\u8be6\u60c5: " + RequestWebUtils.getBaseUrl() + "/health/\n" + StringUtils.subString3(message.getContent(), 500);

               try {
                  Object messageSubType = ReflectionUtils.findEnumObjByName((Class)Objects.requireNonNull(ReflectionUtils.tryClassForName("com.kuma.boot.dingtalk.enums.MessageSubType")), "name", "TEXT");
                  List<Object> requestParam = new ArrayList();
                  requestParam.add(context);
                  requestParam.add(title);
                  Object request = ReflectionUtils.callMethod(ReflectionUtils.tryClassForName("com.kuma.boot.dingtalk.entity.DingerRequest"), "request", requestParam.toArray());
                  List<Object> param = new ArrayList();
                  param.add(messageSubType);
                  param.add(request);
                  ReflectionUtils.callMethod(dingerRobot, "send", param.toArray());
               } catch (Exception e) {
                  LogUtils.error(e);
               }
            }
         }

      }
   }
}
