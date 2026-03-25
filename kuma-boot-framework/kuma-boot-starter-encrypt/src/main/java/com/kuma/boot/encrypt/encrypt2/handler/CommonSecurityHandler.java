package com.kuma.boot.encrypt.encrypt2.handler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.annotation.Security;
import com.kuma.boot.encrypt.encrypt2.codec.SecurityProcessor;
import com.kuma.boot.encrypt.encrypt2.constants.SecurityMode;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import org.springframework.util.Assert;

public class CommonSecurityHandler implements SecurityHandler {
   private SecurityProcessor securityProcessor;
   private SecurityMode securityMode;
   private Charset charset = Charset.forName("UTF-8");

   public CommonSecurityHandler(SecurityProcessor securityProcessor, SecurityMode securityMode, String charsetName) {
      Assert.notNull(securityProcessor, "securityProcessor could not be null");
      this.securityProcessor = securityProcessor;
      this.securityMode = securityMode;
      if (charsetName != null && charsetName.length() > 0) {
         this.charset = Charset.forName(charsetName);
      }

   }

   public Security acquire(Annotation[] annotations) {
      Security security = null;

      for(int i = 0; i < annotations.length; ++i) {
         if (annotations[i] instanceof Security) {
            security = (Security)annotations[i];
            return security;
         }
      }

      return null;
   }

   @Override
   public String handleEncrypt(String source, Annotation ann) {
      Security annotation = (Security) ann;
      if (annotation.encrypt()) {
         try {
            byte[] encrypt = this.securityProcessor.encrypt(source.getBytes(this.charset));
            switch (this.securityMode) {
               case BASE64 -> {
                  return Base64.encode(encrypt);
               }
               case HEX -> {
                  return HexUtil.encodeHexStr(encrypt);
               }
            }
         } catch (Exception e) {
            LogUtils.error("encrypt fail: {}, source field value: {}", new Object[]{e.getMessage(), source});
         }
      }

      return source;
   }

   @Override
   public String handleDecrypt(String source, Annotation ann) {
      Security annotation = (Security) ann;
      if (annotation.decrypt()) {
         try {
            byte[] decrypt = this.securityProcessor.decrypt(source);
            return StrUtil.str(decrypt, this.charset);
         } catch (Exception e) {
            LogUtils.error("decrypt fail: {}, source field value: {}", new Object[]{e.getMessage(), source});
         }
      }

      return source;
   }
}
