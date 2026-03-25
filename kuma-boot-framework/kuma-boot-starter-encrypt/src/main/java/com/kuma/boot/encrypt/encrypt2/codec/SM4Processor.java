package com.kuma.boot.encrypt.encrypt2.codec;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.constants.SecurityMode;
import java.nio.charset.StandardCharsets;

public class SM4Processor implements SecurityProcessor {
   private String secret;
   private SymmetricCrypto symmetricCrypto;

   public SM4Processor(String secret) {
      if (StringUtils.isBlank(secret)) {
         secret = generateKey();
         LogUtils.warn("SM4Processor is not configured with a secret, use randomly generated secret: {}", new Object[]{secret});
      }

      this.secret = secret;
      this.symmetricCrypto = SmUtil.sm4(SecureUtil.decode(secret));
   }

   public byte[] decrypt(String text) {
      return this.symmetricCrypto.decrypt(text);
   }

   public byte[] encrypt(byte[] data) {
      return this.symmetricCrypto.encrypt(data);
   }

   public static String generateKey() {
      return generateKey(SecurityMode.BASE64);
   }

   public static String generateKey(SecurityMode type) {
      byte[] keyBytes = KeyUtil.generateKey("SM4").getEncoded();
      switch (type) {
         case HEX:
            return HexUtil.encodeHexStr(keyBytes);
         case BASE64:
         default:
            return Base64.encode(keyBytes);
      }
   }

   public static void main(String[] args) {
      String secret = generateKey();
      System.out.println("secret = " + secret);
      SM4Processor sm4Processor = new SM4Processor("+6cuvzvyrFZpRG9pf3r7eQ==");
      byte[] encrypt = sm4Processor.encrypt("北京市东城区长安街".getBytes());
      String hexStr = Base64.encode(encrypt);
      System.out.println("decrypt = " + hexStr);
      byte[] decrypt = sm4Processor.decrypt("SSzrI9TBwQeFUf+RCoiyyA==");
      System.out.println("bytes = " + StrUtil.str(decrypt, StandardCharsets.UTF_8));
   }
}
