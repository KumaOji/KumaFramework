package com.kuma.boot.openapi.common.handler.asymmetric;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.kuma.boot.openapi.common.handler.AsymmetricCryHandler;
import java.nio.charset.StandardCharsets;

public class SM2AsymmetricCryHandler implements AsymmetricCryHandler {
   public String sign(String privateKey, String content) {
      byte[] data = content.getBytes(StandardCharsets.UTF_8);
      return this.sign(privateKey, data);
   }

   public String sign(String privateKey, byte[] content) {
      SM2 sm2 = SmUtil.sm2(privateKey, (String)null);
      return sm2.signHexFromHex(HexUtil.encodeHexStr(content));
   }

   public boolean verifySign(String publicKey, String content, String sign) {
      byte[] data = content.getBytes(StandardCharsets.UTF_8);
      return this.verifySign(publicKey, data, sign);
   }

   public boolean verifySign(String publicKey, byte[] content, String sign) {
      SM2 sm2 = SmUtil.sm2((String)null, publicKey);
      return sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
   }

   public String cry(String publicKey, String content) {
      SM2 sm2 = SmUtil.sm2((String)null, publicKey);
      return StrUtil.utf8Str(sm2.encrypt(content, KeyType.PublicKey));
   }

   public byte[] cry(String publicKey, byte[] content) {
      SM2 sm2 = SmUtil.sm2((String)null, publicKey);
      return sm2.encrypt(content, KeyType.PublicKey);
   }

   public String deCry(String privateKey, String content) {
      SM2 sm2 = SmUtil.sm2(privateKey, (String)null);
      return StrUtil.utf8Str(sm2.encrypt(content, KeyType.PrivateKey));
   }

   public byte[] deCry(String privateKey, byte[] content) {
      SM2 sm2 = SmUtil.sm2(privateKey, (String)null);
      return sm2.decrypt(content, KeyType.PrivateKey);
   }
}
