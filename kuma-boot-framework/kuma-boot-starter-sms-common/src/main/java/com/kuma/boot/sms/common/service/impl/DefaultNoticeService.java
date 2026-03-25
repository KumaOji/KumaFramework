package com.kuma.boot.sms.common.service.impl;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.common.exception.NotFindSendHandlerException;
import com.kuma.boot.sms.common.exception.SmsException;
import com.kuma.boot.sms.common.executor.SendAsyncThreadPoolExecutor;
import com.kuma.boot.sms.common.handler.SendHandler;
import com.kuma.boot.sms.common.loadbalancer.ILoadBalancer;
import com.kuma.boot.sms.common.model.NoticeData;
import com.kuma.boot.sms.common.properties.SmsAsyncProperties;
import com.kuma.boot.sms.common.properties.SmsProperties;
import com.kuma.boot.sms.common.service.NoticeService;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class DefaultNoticeService implements NoticeService {
   private final SmsProperties config;
   private final SmsAsyncProperties asyncConfig;
   private final ILoadBalancer smsSenderLoadbalancer;
   private final SendAsyncThreadPoolExecutor executor;

   public DefaultNoticeService(SmsProperties config, SmsAsyncProperties asyncConfig, ILoadBalancer smsSenderLoadbalancer, @Nullable SendAsyncThreadPoolExecutor executor) {
      this.config = config;
      this.asyncConfig = asyncConfig;
      this.smsSenderLoadbalancer = smsSenderLoadbalancer;
      this.executor = executor;
   }

   public boolean phoneRegValidation(String phone) {
      return StringUtils.isNotBlank(phone) && (StringUtils.isBlank(this.config.getReg()) || phone.matches(this.config.getReg()));
   }

   public boolean send(NoticeData noticeData, Collection phones) {
      SendResult result = this.send0(noticeData, phones);
      if (result.exception != null) {
         throw result.exception;
      } else {
         return result.result;
      }
   }

   public void asyncSend(NoticeData noticeData, Collection phones) {
      if (this.asyncConfig.isEnable() && this.executor != null) {
         this.executor.submit(() -> this.send0(noticeData, phones));
      } else {
         this.send(noticeData, phones);
      }
   }

   private SendResult send0(NoticeData noticeData, Collection phones) {
      SendResult result = new SendResult();
      if (phones.isEmpty()) {
         LogUtils.debug("phones is empty", new Object[0]);
         return result;
      } else {
         List<String> phoneList = phones.stream().filter(this::phoneRegValidation).toList();
         if (phoneList.isEmpty()) {
            LogUtils.debug("after filter phones is empty", new Object[0]);
            return result;
         } else {
            SendHandler sendHandler = (SendHandler)this.smsSenderLoadbalancer.choose(noticeData);
            if (sendHandler == null) {
               result.exception = new NotFindSendHandlerException();
            } else {
               try {
                  result.result = sendHandler.send(noticeData, phones);
               } catch (RuntimeException e) {
                  result.exception = e;
               } catch (Exception e) {
                  result.exception = new SmsException(e.getLocalizedMessage(), e);
               }
            }

            if (result.exception != null) {
               LogUtils.debug(result.exception.getLocalizedMessage(), new Object[0]);
            }

            return result;
         }
      }
   }

   private static class SendResult {
      boolean result;
      RuntimeException exception;
   }
}
