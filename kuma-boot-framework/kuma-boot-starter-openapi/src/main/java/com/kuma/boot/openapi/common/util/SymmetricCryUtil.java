package com.kuma.boot.openapi.common.util;

import cn.hutool.crypto.KeyUtil;
import com.kuma.boot.openapi.common.enums.SymmetricCryEnum;
import java.security.SecureRandom;
import java.util.UUID;
import javax.crypto.SecretKey;

public class SymmetricCryUtil {
   public static byte[] getKey(SymmetricCryEnum symmetricCryEnum) {
      String key = UUID.randomUUID().toString();
      SecretKey secretKey = KeyUtil.generateKey(symmetricCryEnum.name(), 128, new SecureRandom(key.getBytes()));
      return secretKey.getEncoded();
   }
}
