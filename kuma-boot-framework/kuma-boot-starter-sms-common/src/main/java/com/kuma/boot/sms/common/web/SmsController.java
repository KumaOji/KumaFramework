package com.kuma.boot.sms.common.web;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sms.common.exception.VerificationCodeIsNullException;
import com.kuma.boot.sms.common.exception.VerifyFailException;
import com.kuma.boot.sms.common.model.NoticeInfo;
import com.kuma.boot.sms.common.model.VerifyInfo;
import com.kuma.boot.sms.common.condition.ConditionalOnSmsEnabled;
import com.kuma.boot.sms.common.service.NoticeService;
import com.kuma.boot.sms.common.service.VerificationCodeService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnSmsEnabled
public class SmsController {
   private final VerificationCodeService verificationCodeService;
   private final NoticeService noticeService;

   public SmsController(VerificationCodeService verificationCodeService, NoticeService noticeService) {
      this.verificationCodeService = verificationCodeService;
      this.noticeService = noticeService;
   }

   public void sendVerificationCode(@PathVariable("phone") String phone) {
      this.verificationCodeService.send(phone);
   }

   public VerifyInfo getVerificationCode(@PathVariable("phone") String phone, @RequestParam(value = "identificationCode",required = false,defaultValue = "") String identificationCode) {
      String code = this.verificationCodeService.find(phone, identificationCode);
      if (StringUtils.isBlank(code)) {
         throw new VerificationCodeIsNullException();
      } else {
         VerifyInfo info = new VerifyInfo();
         info.setCode(code);
         info.setIdentificationCode(identificationCode);
         info.setPhone(phone);
         return info;
      }
   }

   public void verifyVerificationCode(@RequestBody VerifyInfo verifyInfo) {
      if (!this.verificationCodeService.verify(verifyInfo.getPhone(), verifyInfo.getCode(), verifyInfo.getIdentificationCode())) {
         throw new VerifyFailException();
      }
   }

   public void sendNotice(@RequestBody NoticeInfo info) {
      this.noticeService.send(info.getNoticeData(), info.getPhones());
   }
}
