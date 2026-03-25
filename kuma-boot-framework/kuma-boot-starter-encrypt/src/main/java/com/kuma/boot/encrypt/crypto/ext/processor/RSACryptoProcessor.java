package com.kuma.boot.encrypt.crypto.ext.processor;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSACryptoProcessor implements AsymmetricCryptoProcessor {
   private static final Logger log = LoggerFactory.getLogger(RSACryptoProcessor.class);
   private static final String PKCS8_PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----";
   private static final String PKCS8_PUBLIC_KEY_END = "-----END PUBLIC KEY-----";

   public SecretKey createSecretKey() {
      RSA rsa = SecureUtil.rsa();
      SecretKey secretKey = new SecretKey();
      secretKey.setPrivateKey(rsa.getPrivateKeyBase64());
      secretKey.setPublicKey(this.appendPkcs8Padding(rsa.getPublicKeyBase64()));
      return secretKey;
   }

   private String removePkcs8Padding(String key) {
      String result = StringUtils.replace(key, SymbolConstants.NEW_LINE, SymbolConstants.BLANK);
      String[] values = StringUtils.split(result, "-----");
      return ArrayUtils.isNotEmpty(values) ? values[1] : key;
   }

   public String appendPkcs8Padding(String key) {
      return "-----BEGIN PUBLIC KEY-----" + SymbolConstants.NEW_LINE + key + SymbolConstants.NEW_LINE + "-----END PUBLIC KEY-----";
   }

   public String decrypt(String content, String privateKey) {
      byte[] base64Data = Base64.decode(content);
      RSA rsa = SecureUtil.rsa(privateKey, (String)null);
      String result = StrUtil.utf8Str(rsa.decrypt(base64Data, KeyType.PrivateKey));
      log.debug("RSA crypto decrypt data, value is : [{}]", result);
      return result;
   }

   public String encrypt(String content, String publicKey) {
      String key = this.removePkcs8Padding(publicKey);
      RSA rsa = SecureUtil.rsa((String)null, key);
      byte[] encryptedData = rsa.encrypt(content, KeyType.PublicKey);
      String result = Base64.encode(encryptedData);
      log.debug("RSA crypto decrypt data, value is : [{}]", result);
      return result;
   }
}
