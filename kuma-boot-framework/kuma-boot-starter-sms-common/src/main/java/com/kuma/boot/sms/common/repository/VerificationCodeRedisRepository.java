package com.kuma.boot.sms.common.repository;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.common.model.VerificationCode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.Nullable;

public class VerificationCodeRedisRepository implements VerificationCodeRepository {
   private final RedisRepository redisRepository;

   public VerificationCodeRedisRepository(RedisRepository redisRepository) {
      this.redisRepository = redisRepository;
   }

   public VerificationCode findOne(String phone, @Nullable String identificationCode) {
      String key = this.key(phone, identificationCode);
      String value = (String)this.redisRepository.get(key);
      if (StringUtils.isBlank(value)) {
         LogUtils.debug("json data is empty for key: {}", new Object[]{key});
         return null;
      } else {
         return (VerificationCode)JacksonUtils.toObject(value, VerificationCode.class);
      }
   }

   public void save(VerificationCode verificationCode) {
      String key = this.key(verificationCode.getPhone(), verificationCode.getIdentificationCode());
      LocalDateTime expirationTime = verificationCode.getExpirationTime();
      String value = JacksonUtils.toJSONString(verificationCode);
      if (expirationTime == null) {
         this.redisRepository.set(key, value);
      } else {
         long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
         long end = expirationTime.toEpochSecond(ZoneOffset.UTC);
         long timeout = end - now;
         this.redisRepository.setExpire(key, value, timeout, TimeUnit.SECONDS);
      }

   }

   public void delete(String phone, @Nullable String identificationCode) {
      this.redisRepository.del(new String[]{this.key(phone, identificationCode)});
   }

   private String key(String phone, @Nullable String identificationCode) {
      assert identificationCode != null;

      String tempIdentificationCode = StringUtils.trimToNull(identificationCode);
      String var10000 = StringUtils.trimToNull(phone);
      return "SMS:VERIFICATION:CODE:KEY:" + var10000 + ":" + tempIdentificationCode;
   }
}
