package com.kuma.boot.encrypt.encrypt2.codec;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson2.JSONB;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.constants.SecurityMode;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class RSAProcessor implements SecurityProcessor {
   private String publicKey;
   private String privateKey;
   private RSA rsa;

   public RSAProcessor(String publicKey, String privateKey) {
      if (StringUtils.isBlank(publicKey) || StringUtils.isBlank(publicKey)) {
         Map<String, String> map = generateKey();
         publicKey = (String)map.get("PUB");
         privateKey = (String)map.get("PRV");
         LogUtils.warn("RSAProcessor is not configured with a key pair, use randomly generated key pair.\nPublicKey: {}\nPrivateKey: {}", new Object[]{publicKey, privateKey});
      }

      this.publicKey = publicKey;
      this.privateKey = privateKey;
      this.rsa = new RSA(privateKey, publicKey);
   }

   public byte[] encrypt(byte[] data) {
      return this.rsa.encrypt(data, KeyType.PublicKey);
   }

   public byte[] decrypt(String text) {
      return this.rsa.decrypt(text, KeyType.PrivateKey);
   }

   public static Map generateKey() {
      return generateKey(SecurityMode.BASE64);
   }

   public static Map generateKey(SecurityMode type) {
      Map<String, String> map = new HashMap();
      KeyPair pair = KeyUtil.generateKeyPair("RSA");
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
      Map<String, String> map = generateKey();
      String PRV = (String)map.get("PRV");
      System.out.println("PRV = " + PRV);
      String PUB = (String)map.get("PUB");
      System.out.println("PUB = " + PUB);
      String text = "JAVA";
      RSAProcessor converter = new RSAProcessor((String)null, PRV);
      byte[] encrypt = converter.encrypt(JSONB.toBytes(text, StandardCharsets.UTF_8));
      String encodeHexStr = HexUtil.encodeHexStr(encrypt, false);
      System.out.println(encodeHexStr);
      byte[] decrypt = converter.decrypt(encodeHexStr);
      System.out.println(StrUtil.str(decrypt, StandardCharsets.UTF_8));
   }
}
