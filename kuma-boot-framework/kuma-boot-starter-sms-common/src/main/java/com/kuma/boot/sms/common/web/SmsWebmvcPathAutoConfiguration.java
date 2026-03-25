package com.kuma.boot.sms.common.web;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.common.model.NoticeInfo;
import com.kuma.boot.sms.common.model.VerifyInfo;
import java.lang.reflect.Method;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@AutoConfiguration(
   after = {SmsWebmvcAutoConfiguration.class}
)
@ConditionalOnProperty(
   prefix = "kuma.boot.sms.web",
   name = {"enable"},
   havingValue = "true"
)
public class SmsWebmvcPathAutoConfiguration {
   private static String getBasePath(SmsWebmvcProperties properties) {
      String bathPath = StringUtils.trimToNull(properties.getBasePath());
      return bathPath == null ? "/sms" : bathPath;
   }

   @ConditionalOnBean({RequestMappingHandlerMapping.class})
   public void smsController(SmsWebmvcProperties properties, RequestMappingHandlerMapping mapping, SmsController controller) throws NoSuchMethodException, SecurityException {
      String bathPath = getBasePath(properties);
      if (properties.isEnableSend()) {
         Method sendMethod = SmsController.class.getMethod("sendVerificationCode", String.class);
         RequestMappingInfo sendInfo = RequestMappingInfo.paths(new String[]{bathPath + "/verificationCode/{phone}"}).methods(new RequestMethod[]{RequestMethod.POST}).build();
         mapping.registerMapping(sendInfo, controller, sendMethod);
         LogUtils.debug("registerMapping: {}", new Object[]{sendInfo});
      } else {
         LogUtils.debug("not register: sendInfo", new Object[0]);
      }

      if (properties.isEnableGet()) {
         Method getMethod = SmsController.class.getMethod("getVerificationCode", String.class, String.class);
         RequestMappingInfo getInfo = RequestMappingInfo.paths(new String[]{bathPath + "/verificationCode/{phone}"}).methods(new RequestMethod[]{RequestMethod.GET}).produces(new String[]{"application/json"}).build();
         mapping.registerMapping(getInfo, controller, getMethod);
         LogUtils.debug("registerMapping: {}", new Object[]{getInfo});
      } else {
         LogUtils.debug("not register: getInfo", new Object[0]);
      }

      if (properties.isEnableVerify()) {
         Method verifyMethod = SmsController.class.getMethod("verifyVerificationCode", VerifyInfo.class);
         RequestMappingInfo verifyInfo = RequestMappingInfo.paths(new String[]{bathPath + "/verificationCode"}).methods(new RequestMethod[]{RequestMethod.POST}).build();
         mapping.registerMapping(verifyInfo, controller, verifyMethod);
         LogUtils.debug("registerMapping: {}", new Object[]{verifyInfo});
      } else {
         LogUtils.debug("not register: verifyInfo", new Object[0]);
      }

      if (properties.isEnableNotice()) {
         Method noticeMethod = SmsController.class.getMethod("sendNotice", NoticeInfo.class);
         RequestMappingInfo noticeInfo = RequestMappingInfo.paths(new String[]{bathPath + "/notice"}).methods(new RequestMethod[]{RequestMethod.PUT}).build();
         mapping.registerMapping(noticeInfo, controller, noticeMethod);
         LogUtils.debug("registerMapping: {}", new Object[]{noticeInfo});
      } else {
         LogUtils.debug("not register: noticeInfo", new Object[0]);
      }

   }
}
