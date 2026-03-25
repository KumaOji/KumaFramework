package com.kuma.boot.sms.common.configuration;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.common.model.VerificationCodeTypeGenerate;
import com.kuma.boot.sms.common.properties.VerificationCodeMemoryRepositoryProperties;
import com.kuma.boot.sms.common.properties.VerificationCodeProperties;
import com.kuma.boot.sms.common.repository.VerificationCodeMemoryRepository;
import com.kuma.boot.sms.common.repository.VerificationCodeRedisRepository;
import com.kuma.boot.sms.common.repository.VerificationCodeRepository;
import com.kuma.boot.sms.common.service.CodeGenerate;
import com.kuma.boot.sms.common.service.NoticeService;
import com.kuma.boot.sms.common.service.VerificationCodeService;
import com.kuma.boot.sms.common.service.impl.DefaultCodeGenerate;
import com.kuma.boot.sms.common.service.impl.DefaultVerificationCodeService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
   after = {SmsAutoConfiguration.class}
)
@ConditionalOnProperty(
   prefix = "kuma.boot.sms",
   name = {"enabled"},
   havingValue = "true"
)
@EnableConfigurationProperties({VerificationCodeProperties.class, VerificationCodeMemoryRepositoryProperties.class})
public class VerificationCodeAutoConfiguration {
   @Bean
   @ConditionalOnMissingBean
   public CodeGenerate defaultCodeGenerate(VerificationCodeProperties properties) {
      return new DefaultCodeGenerate(properties);
   }

   @Bean
   @ConditionalOnMissingBean
   public VerificationCodeService verificationCodeService(VerificationCodeRepository repository, VerificationCodeProperties properties, NoticeService noticeService, CodeGenerate codeGenerate, ObjectProvider verificationCodeTypeGenerateProvider) {
      return new DefaultVerificationCodeService(repository, properties, noticeService, codeGenerate, (VerificationCodeTypeGenerate)verificationCodeTypeGenerateProvider.getIfUnique());
   }

   @Bean
   @ConditionalOnMissingBean
   @ConditionalOnProperty(
      prefix = "kuma.boot.sms.verification-code",
      name = {"repository"},
      havingValue = "memory"
   )
   public VerificationCodeRepository verificationCodeMemoryRepository(VerificationCodeMemoryRepositoryProperties config) {
      VerificationCodeRepository repository = new VerificationCodeMemoryRepository(config);
      LogUtils.debug("create VerificationCodeRepository: Memory", new Object[0]);
      return repository;
   }

   @Bean
   @ConditionalOnBean({RedisRepository.class})
   @ConditionalOnMissingBean
   @ConditionalOnProperty(
      prefix = "kuma.boot.sms.verification-code",
      name = {"repository"},
      havingValue = "redis"
   )
   public VerificationCodeRepository verificationCodeRedisRepository(RedisRepository redisRepository) {
      VerificationCodeRepository repository = new VerificationCodeRedisRepository(redisRepository);
      LogUtils.debug("create VerificationCodeRepository: Redis", new Object[0]);
      return repository;
   }
}
