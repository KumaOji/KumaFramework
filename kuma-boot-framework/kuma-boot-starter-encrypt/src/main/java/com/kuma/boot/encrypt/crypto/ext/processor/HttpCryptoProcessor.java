package com.kuma.boot.encrypt.crypto.ext.processor;

import cn.hutool.core.util.IdUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.time.Duration;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCryptoProcessor {
   private static final Logger log = LoggerFactory.getLogger(HttpCryptoProcessor.class);
   private final AsymmetricCryptoProcessor asymmetricCryptoProcessor;
   private final SymmetricCryptoProcessor symmetricCryptoProcessor;
   private final RedisRepository redisRepository;

   public HttpCryptoProcessor(AsymmetricCryptoProcessor asymmetricCryptoProcessor, SymmetricCryptoProcessor symmetricCryptoProcessor, RedisRepository redisRepository) {
      this.asymmetricCryptoProcessor = asymmetricCryptoProcessor;
      this.symmetricCryptoProcessor = symmetricCryptoProcessor;
      this.redisRepository = redisRepository;
   }

   public String encrypt(String identity, String content) {
      SecretKey secretKey = this.getSecretKey(identity);
      String result = this.symmetricCryptoProcessor.encrypt(content, secretKey.getSymmetricKey());
      log.debug("Encrypt content from [{}] to [{}].", content, result);
      return result;
   }

   public String decrypt(String identity, String content) {
      SecretKey secretKey = this.getSecretKey(identity);
      String result = this.symmetricCryptoProcessor.decrypt(content, secretKey.getSymmetricKey());
      log.debug("Decrypt content from [{}] to [{}].", content, result);
      return result;
   }

   public SecretKey createSecretKey(String identity, Duration accessTokenValiditySeconds) {
      if (StringUtils.isBlank(identity)) {
         identity = IdUtil.fastUUID();
      }

      SecretKey secretKey = this.asymmetricCryptoProcessor.createSecretKey();
      String symmetricKey = this.symmetricCryptoProcessor.createKey();
      secretKey.setSymmetricKey(symmetricKey);
      secretKey.setIdentity(identity);
      log.debug("Generate secret key, value is : [{}]", secretKey);
      Duration expire = this.getExpire(accessTokenValiditySeconds);
      this.redisRepository.insert(identity, expire.getSeconds(), secretKey);
      return secretKey;
   }

   private boolean isSessionValid(String identity) {
      return this.redisRepository.exists(identity);
   }

   private SecretKey getSecretKey(String identity) {
      if (this.isSessionValid(identity)) {
         SecretKey secretKey = (SecretKey)this.redisRepository.get(identity);
         if (ObjectUtils.isNotEmpty(secretKey)) {
            log.trace("Decrypt Or Encrypt content use param identity [{}], cached identity is [{}].", identity, secretKey.getIdentity());
            return secretKey;
         }
      }

      throw new RuntimeException("SecretKey key is expired!");
   }

   private Duration getExpire(Duration accessTokenValiditySeconds) {
      return !ObjectUtils.isEmpty(accessTokenValiditySeconds) && !accessTokenValiditySeconds.isZero() ? accessTokenValiditySeconds : Duration.ofHours(2L);
   }

   private String decryptFrontendPublicKey(String content, String privateKey) {
      String frontendPublicKey = this.asymmetricCryptoProcessor.decrypt(content, privateKey);
      log.debug("Decrypt frontend public key, value is : [{}]", frontendPublicKey);
      return frontendPublicKey;
   }

   private String encryptBackendKey(String symmetricKey, String publicKey) {
      String encryptedAesKey = this.asymmetricCryptoProcessor.encrypt(symmetricKey, publicKey);
      log.debug("Encrypt symmetric key use frontend public key, value is : [{}]", encryptedAesKey);
      return encryptedAesKey;
   }

   public String exchange(String identity, String confidential) {
      SecretKey secretKey = this.getSecretKey(identity);
      String frontendPublicKey = this.decryptFrontendPublicKey(confidential, secretKey.getPrivateKey());
      return this.encryptBackendKey(secretKey.getSymmetricKey(), frontendPublicKey);
   }
}
