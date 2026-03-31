package com.kuma.boot.monitor.alarm.core.loader.helper;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.monitor.alarm.core.exception.RegisterInfoIllegalException;
import com.kuma.boot.monitor.alarm.core.loader.entity.RegisterInfo;
import com.kuma.boot.monitor.alarm.core.loader.util.PropertiesUtil;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterInfoLoaderHelper {
   private static final Logger log = LoggerFactory.getLogger(RegisterInfoLoaderHelper.class);
   private static final String DEFAULT_CONF_NAME = "alarm.properties";
   private static volatile RegisterInfo instance;

   public RegisterInfoLoaderHelper() {
   }

   public static RegisterInfo load() {
      if (instance == null) {
         synchronized(RegisterInfoLoaderHelper.class) {
            if (instance == null) {
               try {
                  Properties properties = PropertiesUtil.read("alarm.properties");
                  RegisterInfo info = new RegisterInfo();
                  PropertiesUtil.copy(properties, info);
                  checkRegisterInfo(info);
                  instance = info;
               } catch (RegisterInfoIllegalException ex) {
                  log.error("illegal register info: {}", ex);
                  return null;
               } catch (Exception e) {
                  log.error("load register info error: {}", e);
                  return null;
               }
            }
         }
      }

      return instance;
   }

   private static void checkRegisterInfo(RegisterInfo registerInfo) {
      if (StringUtils.isBlank(registerInfo.getAlarmConfPath())) {
         throw new RegisterInfoIllegalException("alarmConfPath should not be null or empty!");
      } else if (StringUtils.isBlank(registerInfo.getAppName())) {
         throw new RegisterInfoIllegalException("appName should not be null or empty!");
      } else if (StringUtils.isBlank(registerInfo.getDefaultAlarmUsers())) {
         throw new RegisterInfoIllegalException("defaultAlarmUser should not be null or empty!");
      } else {
         if (registerInfo.getMaxAlarmType() == null) {
            registerInfo.setMaxAlarmType(1000);
         }

      }
   }
}
