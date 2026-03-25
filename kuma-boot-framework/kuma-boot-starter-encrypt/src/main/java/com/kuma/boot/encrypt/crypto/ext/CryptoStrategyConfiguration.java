package com.kuma.boot.encrypt.crypto.ext;

import com.kuma.boot.encrypt.crypto.ext.annotation.ConditionalOnSMCrypto;
import com.kuma.boot.encrypt.crypto.ext.annotation.ConditionalOnStandardCrypto;
import com.kuma.boot.encrypt.crypto.ext.processor.AESCryptoProcessor;
import com.kuma.boot.encrypt.crypto.ext.processor.AsymmetricCryptoProcessor;
import com.kuma.boot.encrypt.crypto.ext.processor.RSACryptoProcessor;
import com.kuma.boot.encrypt.crypto.ext.processor.SM2CryptoProcessor;
import com.kuma.boot.encrypt.crypto.ext.processor.SM4CryptoProcessor;
import com.kuma.boot.encrypt.crypto.ext.processor.SymmetricCryptoProcessor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(
   proxyBeanMethods = false
)
public class CryptoStrategyConfiguration {
   private static final Logger log = LoggerFactory.getLogger(HttpCryptoConfiguration.class);

   @PostConstruct
   public void postConstruct() {
      log.debug("SDK [Engine Asymmetric Crypto] Auto Configure.");
   }

   @Configuration(
      proxyBeanMethods = false
   )
   @ConditionalOnSMCrypto
   static class SMCryptoConfiguration {
      @Bean
      @ConditionalOnMissingBean
      public AsymmetricCryptoProcessor sm2CryptoProcessor() {
         SM2CryptoProcessor sm2CryptoProcessor = new SM2CryptoProcessor();
         CryptoStrategyConfiguration.log.debug("Strategy [SM Asymmetric SM2 Crypto Processor] Auto Configure.");
         return sm2CryptoProcessor;
      }

      @Bean
      @ConditionalOnMissingBean
      public SymmetricCryptoProcessor sm4CryptoProcessor() {
         SM4CryptoProcessor sm4CryptoProcessor = new SM4CryptoProcessor();
         CryptoStrategyConfiguration.log.debug("Strategy [SM Symmetric SM4 Crypto Processor] Auto Configure.");
         return sm4CryptoProcessor;
      }
   }

   @Configuration(
      proxyBeanMethods = false
   )
   @ConditionalOnStandardCrypto
   static class StandardCryptoConfiguration {
      @Bean
      @ConditionalOnMissingBean
      public AsymmetricCryptoProcessor rsaCryptoProcessor() {
         RSACryptoProcessor rsaCryptoProcessor = new RSACryptoProcessor();
         CryptoStrategyConfiguration.log.debug("Strategy [Standard Asymmetric RSA Crypto Processor] Auto Configure.");
         return rsaCryptoProcessor;
      }

      @Bean
      @ConditionalOnMissingBean
      public SymmetricCryptoProcessor aesCryptoProcessor() {
         AESCryptoProcessor aesCryptoProcessor = new AESCryptoProcessor();
         CryptoStrategyConfiguration.log.debug("Strategy [Standard Symmetric AES Crypto Processor] Auto Configure.");
         return aesCryptoProcessor;
      }
   }
}
