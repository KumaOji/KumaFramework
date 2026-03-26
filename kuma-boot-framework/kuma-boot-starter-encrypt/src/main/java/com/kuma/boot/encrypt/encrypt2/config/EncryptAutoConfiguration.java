package com.kuma.boot.encrypt.encrypt2.config;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.advice.DecryptRequestAdvice;
import com.kuma.boot.encrypt.encrypt2.advice.EncryptResponseAdvice;
import com.kuma.boot.encrypt.encrypt2.aspect.SecurityAspect;
import com.kuma.boot.encrypt.encrypt2.codec.AESProcessor;
import com.kuma.boot.encrypt.encrypt2.codec.RSAProcessor;
import com.kuma.boot.encrypt.encrypt2.codec.SM2Processor;
import com.kuma.boot.encrypt.encrypt2.codec.SM4Processor;
import com.kuma.boot.encrypt.encrypt2.codec.SecurityProcessor;
import com.kuma.boot.encrypt.encrypt2.config.properties.SecurityProperties;
import com.kuma.boot.encrypt.encrypt2.format.CommonSensitiveProcessor;
import com.kuma.boot.encrypt.encrypt2.format.SensitiveProcessor;
import com.kuma.boot.encrypt.encrypt2.handler.CommonSecurityHandler;
import com.kuma.boot.encrypt.encrypt2.handler.CommonSensitiveHandler;
import com.kuma.boot.encrypt.encrypt2.handler.SecurityHandler;
import com.kuma.boot.encrypt.encrypt2.handler.SensitiveHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("encrypt2AutoConfiguration")
@ConditionalOnWebApplication
@EnableConfigurationProperties({SecurityProperties.class})
public class EncryptAutoConfiguration {
   private SecurityProperties securityProperties;

   public EncryptAutoConfiguration(SecurityProperties securityProperties) {
      this.securityProperties = securityProperties;
   }

   @Bean
   @ConditionalOnMissingBean
   @ConditionalOnProperty(
      prefix = "fzy.security",
      name = {"type"},
      havingValue = "AES"
   )
   public SecurityProcessor aesProcessor() {
      return new AESProcessor(this.securityProperties.getSecret());
   }

   @Bean
   @ConditionalOnMissingBean
   @ConditionalOnProperty(
      prefix = "fzy.security",
      name = {"type"},
      havingValue = "RSA"
   )
   public SecurityProcessor rsaProcessor() {
      return new RSAProcessor(this.securityProperties.getPublicKey(), this.securityProperties.getPrivateKey());
   }

   @Bean
   @ConditionalOnMissingBean
   @ConditionalOnProperty(
      prefix = "fzy.security",
      name = {"type"},
      havingValue = "SM4"
   )
   public SecurityProcessor sm4Processor() {
      return new SM4Processor(this.securityProperties.getSecret());
   }

   @Bean
   @ConditionalOnMissingBean
   @ConditionalOnProperty(
      prefix = "fzy.security",
      name = {"type"},
      havingValue = "SM2"
   )
   public SecurityProcessor sm2Processor() {
      return new SM2Processor(this.securityProperties.getPublicKey(), this.securityProperties.getPrivateKey());
   }

   @Bean
   @ConditionalOnMissingBean
   public SecurityHandler commonSecurityHandler(@Autowired SecurityProcessor aesProcessor) {
      LogUtils.debug("encrypt-starter loading SecurityProcessor: {}", new Object[]{aesProcessor.getClass().getName()});
      return new CommonSecurityHandler(aesProcessor, this.securityProperties.getMode(), this.securityProperties.getCharset());
   }

   @Bean
   @ConditionalOnMissingBean
   public SensitiveProcessor commonSensitiveProcessor() {
      return new CommonSensitiveProcessor();
   }

   @Bean
   @ConditionalOnMissingBean
   public SensitiveHandler commonSensitiveHandler(@Autowired SensitiveProcessor commonSensitiveProcessor) {
      LogUtils.debug("encrypt-starter loading SensitiveHandler: {}", new Object[]{commonSensitiveProcessor.getClass().getName()});
      return new CommonSensitiveHandler(commonSensitiveProcessor);
   }

   @Bean
   @ConditionalOnMissingBean
   public DecryptRequestAdvice decryptRequestAdvice() {
      return new DecryptRequestAdvice(this.securityProperties.getMaxDeep(), this.securityProperties.getClassPackage());
   }

   @Bean
   @ConditionalOnMissingBean
   public EncryptResponseAdvice encryptResponseAdvice() {
      List<String> classPackage = this.securityProperties.getClassPackage();
      LogUtils.debug("encrypt-starter scan class packages: {}", new Object[]{classPackage});
      return new EncryptResponseAdvice(this.securityProperties.getMaxDeep(), classPackage);
   }

   @Bean
   @ConditionalOnMissingBean
   @ConditionalOnProperty(
      prefix = "fzy.security",
      name = {"enable"},
      havingValue = "true"
   )
   public SecurityAspect securityAspect(@Autowired DecryptRequestAdvice decryptRequestAdvice, @Autowired EncryptResponseAdvice encryptResponseAdvice) {
      return new SecurityAspect(decryptRequestAdvice, encryptResponseAdvice);
   }
}
