package com.kuma.boot.openapi.common.handler.asymmetric;

import cn.hutool.crypto.SignUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.kuma.boot.openapi.common.handler.AsymmetricCryHandler;
import com.kuma.boot.openapi.common.util.Base64Util;
import java.nio.charset.StandardCharsets;

public class RSAAsymmetricCryHandler implements AsymmetricCryHandler {
   public String sign(String privateKey, String content) {
      byte[] data = content.getBytes(StandardCharsets.UTF_8);
      return this.sign(privateKey, data);
   }

   public String sign(String privateKey, byte[] content) {
      Sign sign = SignUtil.sign(SignAlgorithm.SHA256withRSA, privateKey, (String)null);
      byte[] signed = sign.sign(content);
      return Base64Util.bytesToBase64(signed);
   }

   public boolean verifySign(String publicKey, String content, String sign) {
      byte[] data = content.getBytes(StandardCharsets.UTF_8);
      return this.verifySign(publicKey, data, sign);
   }

   public boolean verifySign(String publicKey, byte[] content, String sign) {
      Sign signObj = SignUtil.sign(SignAlgorithm.SHA256withRSA, (String)null, publicKey);
      return signObj.verify(content, Base64Util.base64ToBytes(sign));
   }

   public String cry(String publicKey, String content) {
      byte[] data = content.getBytes(StandardCharsets.UTF_8);
      byte[] encrypt = this.cry(publicKey, data);
      return Base64Util.bytesToBase64(encrypt);
   }

   public byte[] cry(String publicKey, byte[] content) {
      RSA rsa = new RSA((String)null, publicKey);
      return rsa.encrypt(content, KeyType.PublicKey);
   }

   public String deCry(String privateKey, String content) {
      byte[] dataBytes = Base64Util.base64ToBytes(content);
      byte[] decrypt = this.deCry(privateKey, dataBytes);
      return new String(decrypt, StandardCharsets.UTF_8);
   }

   public byte[] deCry(String privateKey, byte[] content) {
      RSA rsa = new RSA(privateKey);
      return rsa.decrypt(content, KeyType.PrivateKey);
   }
}
