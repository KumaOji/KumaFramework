package com.kuma.boot.encrypt.crypto.ext.processor;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.crypto.engines.SM2Engine.Mode;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SM2CryptoProcessor implements AsymmetricCryptoProcessor {
   private static final Logger log = LoggerFactory.getLogger(SM2CryptoProcessor.class);
   private static final String FLAG = "04";

   public SecretKey createSecretKey() {
      SM2 sm2 = SmUtil.sm2();
      sm2.setMode(Mode.C1C3C2);
      String privateKey = HexUtil.encodeHexStr(BCUtil.encodeECPrivateKey(sm2.getPrivateKey()));
      String publicKey = HexUtil.encodeHexStr(((BCECPublicKey)sm2.getPublicKey()).getQ().getEncoded(false));
      SecretKey secretKey = new SecretKey();
      secretKey.setPrivateKey(privateKey);
      secretKey.setPublicKey(publicKey);
      return secretKey;
   }

   public String decrypt(String content, String privateKey) {
      SM2 sm2 = SmUtil.sm2(privateKey, (String)null);
      sm2.setMode(Mode.C1C3C2);
      String result = StrUtil.utf8Str(sm2.decrypt(content, KeyType.PrivateKey));
      log.debug("SM2 crypto decrypt data, value is : [{}]", result);
      return result;
   }

   public String encrypt(String content, String publicKey) {
      SM2 sm2 = SmUtil.sm2((String)null, publicKey);
      String result = new String(sm2.encrypt(content, KeyType.PublicKey));
      log.debug("SM2 crypto encrypt data, value is : [{}]", result);
      return result;
   }
}
