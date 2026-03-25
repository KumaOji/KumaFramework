package com.kuma.boot.encrypt.encrypt2.codec;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.constants.SecurityMode;

public class AESProcessor implements SecurityProcessor {
   private String secret;
   private AES aes;

   public AESProcessor(String secret) {
      if (StringUtils.isBlank(secret)) {
         secret = generateKey();
         LogUtils.warn("AESProcessor is not configured with a secret, use randomly generated secret: {}", new Object[]{secret});
      }

      this.secret = secret;
      this.aes = SecureUtil.aes(SecureUtil.decode(secret));
   }

   public byte[] decrypt(String text) {
      return this.aes.decrypt(text);
   }

   public byte[] encrypt(byte[] data) {
      return this.aes.encrypt(data);
   }

   public static String generateKey() {
      return generateKey(SecurityMode.BASE64);
   }

   public static String generateKey(SecurityMode type) {
      byte[] keyBytes = KeyUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
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
      System.out.println(secret);
      AESProcessor processor = new AESProcessor((String)null);
      byte[] encrypt = processor.encrypt("Java".getBytes());
      String encode1 = Base64.encode(encrypt);
      System.out.println(encode1);
      byte[] decrypt = processor.decrypt(encode1);
      System.out.println(StrUtil.str(decrypt, "UTF-8"));
   }
}
