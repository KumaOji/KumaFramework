package com.kuma.boot.encrypt.sign.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.sign.bean.ISecurityInfo;
import com.kuma.boot.encrypt.sign.exception.EncryptBodyFailException;
import com.kuma.boot.encrypt.sign.exception.IllegalSecurityTypeException;
import com.kuma.boot.encrypt.sign.exception.KeyNotConfiguredException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class CommonUtils {
   public static String checkAndGetKey(String k1, String k2, String keyName) {
      if (StrUtil.isEmpty(k1) && StrUtil.isEmpty(k2)) {
         throw new KeyNotConfiguredException(String.format("%s is not configured (未配置%s)", keyName, keyName));
      } else {
         return k1 == null ? k2 : k1;
      }
   }

   public static RSA infoBeanToRsaInstance(ISecurityInfo info) {
      RSA var10000;
      switch (info.getRsaKeyType()) {
         case PUBLIC -> var10000 = new RSA((byte[])null, SecureUtil.decode(info.getKey()));
         case PRIVATE -> var10000 = new RSA(SecureUtil.decode(info.getKey()), (byte[])null);
         default -> throw new IllegalSecurityTypeException();
      }

      return var10000;
   }

   public static boolean isConvertToString(Class clazz) {
      return clazz.equals(String.class) || ClassUtil.isPrimitiveWrapper(clazz);
   }

   public static String convertToStringOrJson(Object val, JsonMapper mapper) {
      if (isConvertToString(val.getClass())) {
         return String.valueOf(val);
      } else {
         try {
            return mapper.writeValueAsString(val);
         } catch (JacksonException e) {
            LogUtils.error(e);
            throw new EncryptBodyFailException(e.getMessage());
         }
      }
   }
}
