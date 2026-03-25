package com.kuma.boot.encrypt.handler.impl;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.exception.EncryptException;
import com.kuma.boot.encrypt.handler.EncryptHandler;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptHandler implements EncryptHandler {
   private String secret;
   private static final String IV_PARA = "0102030405060708";
   private static final String KEY_ALGORITHM = "AES";

   public byte[] encode(byte[] content) {
      try {
         IvParameterSpec zeroIv = new IvParameterSpec("0102030405060708".getBytes());
         SecretKeySpec key = new SecretKeySpec(this.secret.getBytes(), "AES");
         Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
         cipher.init(1, key, zeroIv);
         byte[] byteAes = cipher.doFinal(content);
         return Base64.getEncoder().encode(byteAes);
      } catch (Exception e) {
         LogUtils.error(e.getMessage(), new Object[0]);
         throw new EncryptException("rsa加密错误", e);
      }
   }

   public byte[] decode(byte[] content) {
      try {
         IvParameterSpec zeroIv = new IvParameterSpec("0102030405060708".getBytes());
         SecretKeySpec key = new SecretKeySpec(this.secret.getBytes(), "AES");
         Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
         cipher.init(2, key, zeroIv);
         byte[] byteContent = Base64.getDecoder().decode(content);
         return cipher.doFinal(byteContent);
      } catch (Exception e) {
         throw new EncryptException("rsa加密错误", e);
      }
   }
}
