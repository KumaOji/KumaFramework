package com.kuma.boot.encrypt.handler.impl;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.entity.RsaKey;
import com.kuma.boot.encrypt.exception.EncryptException;
import com.kuma.boot.encrypt.handler.EncryptHandler;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RsaEncryptHandler implements EncryptHandler {
   private static final String KEY_ALGORITHM = "RSA";
   private static final int KEY_SIZE = 1024;
   private static final String PUBLIC_KEY = "RSAPublicKey";
   private static final String PRIVATE_KEY = "RSAPrivateKey";
   private static final int MAX_ENCODE_BLOCK = 117;
   private static final int MAX_DECODE_BLOCK = 128;
   private String publicKey;
   private String privateKey;

   public byte[] encode(byte[] content) {
      try {
         return encryptByPublicKey(content, Base64.getDecoder().decode(this.publicKey));
      } catch (Exception e) {
         LogUtils.error(e.getMessage(), new Object[0]);
         throw new EncryptException("rsa加密错误", e);
      }
   }

   public byte[] decode(byte[] content) {
      try {
         return decryptByPrivateKey(Base64.getDecoder().decode(content), Base64.getDecoder().decode(this.privateKey));
      } catch (Exception e) {
         LogUtils.error(e.getMessage(), new Object[0]);
         throw new EncryptException("rsa解密错误", e);
      }
   }

   public static RsaKey getRsaKeys() throws Exception {
      Map<String, Object> keyMap = initKey();
      byte[] publicKey = getPublicKey(keyMap);
      byte[] privateKey = getPrivateKey(keyMap);
      RsaKey rsaKey = new RsaKey();
      rsaKey.setPublicKey(Base64.getEncoder().encodeToString(publicKey));
      rsaKey.setPrivateKey(Base64.getEncoder().encodeToString(privateKey));
      return rsaKey;
   }

   private static Map initKey() throws Exception {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(1024);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
      RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
      Map<String, Object> keyMap = new HashMap();
      keyMap.put("RSAPublicKey", publicKey);
      keyMap.put("RSAPrivateKey", privateKey);
      return keyMap;
   }

   private static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
      byte[] encryptedData = new byte[0];
      if (data.length == 0) {
         return encryptedData;
      } else {
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(1, privateKey);
            encryptedData = Base64.getEncoder().encode(doFinal(data, cipher, out, 117));
         } catch (Throwable var9) {
            try {
               out.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         out.close();
         return encryptedData;
      }
   }

   private static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
      byte[] encryptedData = new byte[0];
      if (data.length == 0) {
         return encryptedData;
      } else {
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(1, pubKey);
            encryptedData = Base64.getEncoder().encode(doFinal(data, cipher, out, 117));
         } catch (Throwable var9) {
            try {
               out.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         out.close();
         return encryptedData;
      }
   }

   private static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
      byte[] encryptedData = new byte[0];
      if (data.length == 0) {
         return encryptedData;
      } else {
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(2, privateKey);
            encryptedData = doFinal(data, cipher, out, 128);
         } catch (Throwable var9) {
            try {
               out.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         out.close();
         return encryptedData;
      }
   }

   private static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
      byte[] encryptedData = new byte[0];
      if (data.length == 0) {
         return encryptedData;
      } else {
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(2, pubKey);
            encryptedData = doFinal(data, cipher, out, 128);
         } catch (Throwable var9) {
            try {
               out.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         out.close();
         return encryptedData;
      }
   }

   private static byte[] getPrivateKey(Map keyMap) {
      Key key = (Key)keyMap.get("RSAPrivateKey");
      return key.getEncoded();
   }

   private static byte[] getPublicKey(Map keyMap) throws Exception {
      Key key = (Key)keyMap.get("RSAPublicKey");
      return key.getEncoded();
   }

   private static byte[] doFinal(byte[] data, Cipher cipher, ByteArrayOutputStream out, int MAX_BLOCK) throws BadPaddingException, IllegalBlockSizeException {
      int inputLen = data.length;
      int offSet = 0;

      for(int i = 0; inputLen - offSet > 0; offSet = i * MAX_BLOCK) {
         byte[] cache;
         if (inputLen - offSet > MAX_BLOCK) {
            cache = cipher.doFinal(data, offSet, MAX_BLOCK);
         } else {
            cache = cipher.doFinal(data, offSet, inputLen - offSet);
         }

         out.write(cache, 0, cache.length);
         ++i;
      }

      return out.toByteArray();
   }
}
