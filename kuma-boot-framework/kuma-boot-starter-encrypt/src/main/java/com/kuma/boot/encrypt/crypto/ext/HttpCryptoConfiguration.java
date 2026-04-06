package com.kuma.boot.encrypt.crypto.ext;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.encrypt.crypto.ext.enhance.DecryptRequestBodyAdvice;
import com.kuma.boot.encrypt.crypto.ext.enhance.DecryptRequestParamMapResolver;
import com.kuma.boot.encrypt.crypto.ext.enhance.DecryptRequestParamResolver;
import com.kuma.boot.encrypt.crypto.ext.enhance.EncryptResponseBodyAdvice;
import com.kuma.boot.encrypt.crypto.ext.processor.AsymmetricCryptoProcessor;
import com.kuma.boot.encrypt.crypto.ext.processor.HttpCryptoProcessor;
import com.kuma.boot.encrypt.crypto.ext.processor.SymmetricCryptoProcessor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(
   proxyBeanMethods = false
)
@ConditionalOnProperty(prefix = "kuma.boot.crypto", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({CryptoProperties.class})
@Import({CryptoStrategyConfiguration.class})
public class HttpCryptoConfiguration {
   private static final Logger log = LoggerFactory.getLogger(HttpCryptoConfiguration.class);

   @PostConstruct
   public void postConstruct() {
      log.debug("SDK [Engine Rest Crypto] Auto Configure.");
   }

   @Bean
   @ConditionalOnClass({RedisRepository.class})
   @ConditionalOnBean({RedisRepository.class})
   @ConditionalOnMissingBean
   public HttpCryptoProcessor interfaceCryptoProcessor(AsymmetricCryptoProcessor asymmetricCryptoProcessor, SymmetricCryptoProcessor symmetricCryptoProcessor, RedisRepository redisRepository) {
      HttpCryptoProcessor httpCryptoProcessor = new HttpCryptoProcessor(asymmetricCryptoProcessor, symmetricCryptoProcessor, redisRepository);
      log.trace("Bean [Interface Crypto Processor] Auto Configure.");
      return httpCryptoProcessor;
   }

   @Bean
   @ConditionalOnClass({HttpCryptoProcessor.class})
   @ConditionalOnMissingBean
   public DecryptRequestBodyAdvice decryptRequestBodyAdvice(HttpCryptoProcessor httpCryptoProcessor) {
      DecryptRequestBodyAdvice decryptRequestBodyAdvice = new DecryptRequestBodyAdvice();
      decryptRequestBodyAdvice.setInterfaceCryptoProcessor(httpCryptoProcessor);
      log.trace("Bean [Decrypt Request Body Advice] Auto Configure.");
      return decryptRequestBodyAdvice;
   }

   @Bean
   @ConditionalOnClass({HttpCryptoProcessor.class})
   @ConditionalOnMissingBean
   public EncryptResponseBodyAdvice encryptResponseBodyAdvice(HttpCryptoProcessor httpCryptoProcessor) {
      EncryptResponseBodyAdvice encryptResponseBodyAdvice = new EncryptResponseBodyAdvice();
      encryptResponseBodyAdvice.setInterfaceCryptoProcessor(httpCryptoProcessor);
      log.trace("Bean [Encrypt Response Body Advice] Auto Configure.");
      return encryptResponseBodyAdvice;
   }

   @Bean
   @ConditionalOnClass({HttpCryptoProcessor.class})
   @ConditionalOnMissingBean
   public DecryptRequestParamMapResolver decryptRequestParamStringResolver(HttpCryptoProcessor httpCryptoProcessor) {
      DecryptRequestParamMapResolver decryptRequestParamMapResolver = new DecryptRequestParamMapResolver();
      decryptRequestParamMapResolver.setInterfaceCryptoProcessor(httpCryptoProcessor);
      log.trace("Bean [Decrypt Request Param Map Resolver] Auto Configure.");
      return decryptRequestParamMapResolver;
   }

   @Bean
   @ConditionalOnClass({HttpCryptoProcessor.class})
   @ConditionalOnMissingBean
   public DecryptRequestParamResolver decryptRequestParamResolver(HttpCryptoProcessor httpCryptoProcessor) {
      DecryptRequestParamResolver decryptRequestParamResolver = new DecryptRequestParamResolver();
      decryptRequestParamResolver.setInterfaceCryptoProcessor(httpCryptoProcessor);
      log.trace("Bean [Decrypt Request Param Resolver] Auto Configure.");
      return decryptRequestParamResolver;
   }
}
