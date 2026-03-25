package com.kuma.boot.encrypt.encrypt2.codec;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.constants.SecurityMode;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class SM2Processor implements SecurityProcessor {
   private String publicKey;
   private String privateKey;
   private SM2 sm2;

   public SM2Processor(String publicKey, String privateKey) {
      if (StringUtils.isBlank(publicKey) || StringUtils.isBlank(publicKey)) {
         Map<String, String> map = generateKey();
         publicKey = (String)map.get("PUB");
         privateKey = (String)map.get("PRV");
         LogUtils.warn("SM2Processor is not configured with a key pair, use randomly generated key pair.\nPublicKey: {}\nPrivateKey: {}", new Object[]{publicKey, privateKey});
      }

      this.publicKey = publicKey;
      this.privateKey = privateKey;
      this.sm2 = SmUtil.sm2(privateKey, publicKey);
   }

   public byte[] encrypt(byte[] data) {
      return this.sm2.encrypt(data, KeyType.PublicKey);
   }

   public byte[] decrypt(String text) {
      return this.sm2.decrypt(text, KeyType.PrivateKey);
   }

   public static Map generateKey() {
      return generateKey(SecurityMode.BASE64);
   }

   public static Map generateKey(SecurityMode type) {
      Map<String, String> map = new HashMap();
      KeyPair pair = KeyUtil.generateKeyPair("SM2");
      String PRV;
      String PUB;
      switch (type) {
         case HEX:
            PRV = HexUtil.encodeHexStr(pair.getPrivate().getEncoded(), false);
            PUB = HexUtil.encodeHexStr(pair.getPublic().getEncoded(), false);
            break;
         case BASE64:
         default:
            PRV = Base64.encode(pair.getPrivate().getEncoded());
            PUB = Base64.encode(pair.getPublic().getEncoded());
      }

      map.put("PUB", PUB);
      map.put("PRV", PRV);
      return map;
   }

   public static void main(String[] args) {
      Map<String, String> map = generateKey(SecurityMode.HEX);
      String privateKey = (String)map.get("PRV");
      System.out.println("privateKey = " + privateKey);
      String publicKey = (String)map.get("PUB");
      System.out.println("publicKey = " + publicKey);
      SM2Processor sm2Processor = new SM2Processor(publicKey, privateKey);
      String encode = Base64.encode(sm2Processor.encrypt("Java".getBytes()));
      System.out.println("加密后：" + encode);
      byte[] decrypt = sm2Processor.decrypt(encode);
      System.out.println("解密后：" + StrUtil.str(decrypt, StandardCharsets.UTF_8));
   }
}
