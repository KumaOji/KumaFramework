package com.kuma.boot.encrypt.crypto.ext.processor;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESCryptoProcessor implements SymmetricCryptoProcessor {
   private static final Logger log = LoggerFactory.getLogger(AESCryptoProcessor.class);

   public String createKey() {
      return RandomUtil.randomStringUpper(16);
   }

   public String decrypt(String data, String key) {
      AES aes = SecureUtil.aes(StrUtil.utf8Str(key).getBytes(StandardCharsets.UTF_8));
      byte[] result = aes.decrypt(Base64.decode(StrUtil.utf8Str(data).getBytes(StandardCharsets.UTF_8)));
      log.debug("AES crypto decrypt data, value is : [{}]", result);
      return StrUtil.utf8Str(result);
   }

   public String encrypt(String data, String key) {
      AES aes = SecureUtil.aes(StrUtil.utf8Str(key).getBytes(StandardCharsets.UTF_8));
      byte[] result = aes.encrypt(StrUtil.utf8Str(data).getBytes(StandardCharsets.UTF_8));
      log.debug("AES crypto encrypt data, value is : [{}]", result);
      return StrUtil.utf8Str(result);
   }
}
