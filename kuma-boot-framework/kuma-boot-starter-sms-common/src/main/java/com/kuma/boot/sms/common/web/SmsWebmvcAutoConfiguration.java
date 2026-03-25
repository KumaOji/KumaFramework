package com.kuma.boot.sms.common.web;

import com.kuma.boot.sms.common.configuration.SmsAutoConfiguration;
import com.kuma.boot.sms.common.service.NoticeService;
import com.kuma.boot.sms.common.service.VerificationCodeService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
   after = {SmsAutoConfiguration.class}
)
@EnableConfigurationProperties({SmsWebmvcProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.sms.web",
   name = {"enable"},
   havingValue = "true"
)
public class SmsWebmvcAutoConfiguration {
   @Bean
   @ConditionalOnMissingBean({SmsController.class})
   public SmsController smsController(VerificationCodeService verificationCodeService, NoticeService noticeService) {
      return new SmsController(verificationCodeService, noticeService);
   }
}
