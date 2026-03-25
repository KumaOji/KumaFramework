package com.kuma.boot.encrypt.encrypt1.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
   private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

   private static Cipher getCipher(byte[] key, int model) throws Exception {
      SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(model, secretKeySpec);
      return cipher;
   }

   public static String encrypt(byte[] data, byte[] key) throws Exception {
      Cipher cipher = getCipher(key, 1);
      return Base64.getEncoder().encodeToString(cipher.doFinal(data));
   }

   public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
      Cipher cipher = getCipher(key, 2);
      return cipher.doFinal(Base64.getDecoder().decode(data));
   }
}
