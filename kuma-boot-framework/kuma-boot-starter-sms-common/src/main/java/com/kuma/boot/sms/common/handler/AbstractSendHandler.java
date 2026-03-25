package com.kuma.boot.sms.common.handler;

import com.kuma.boot.sms.common.event.SmsSendFailEvent;
import com.kuma.boot.sms.common.event.SmsSendFinallyEvent;
import com.kuma.boot.sms.common.event.SmsSendSuccessEvent;
import com.kuma.boot.sms.common.model.NoticeData;
import com.kuma.boot.sms.common.properties.AbstractHandlerProperties;
import java.util.Collection;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;

public abstract class AbstractSendHandler implements SendHandler {
   protected final AbstractHandlerProperties properties;
   private final ApplicationEventPublisher eventPublisher;

   public AbstractSendHandler(AbstractHandlerProperties properties, @Nullable ApplicationEventPublisher eventPublisher) {
      this.properties = properties;
      this.eventPublisher = eventPublisher;
   }

   public boolean acceptSend(@Nullable String type) {
      return this.properties.getTemplates().containsKey(type);
   }

   public abstract String getChannelName();

   protected final void publishSendSuccessEvent(NoticeData noticeData, Collection phones, Object response) {
      if (this.eventPublisher != null) {
         this.eventPublisher.publishEvent(new SmsSendSuccessEvent(this, this.getChannelName(), phones, noticeData.getType(), noticeData.getParams(), response));
         this.publishSendFinallyEvent(noticeData, phones, response);
      }
   }

   protected final void publishSendFailEvent(NoticeData noticeData, Collection phones, Throwable cause, Object response) {
      if (this.eventPublisher != null) {
         this.eventPublisher.publishEvent(new SmsSendFailEvent(this, this.getChannelName(), phones, noticeData.getType(), noticeData.getParams(), cause, response));
         this.publishSendFinallyEvent(noticeData, phones, response);
      }
   }

   private void publishSendFinallyEvent(NoticeData noticeData, Collection phones, Object response) {
      if (this.eventPublisher != null) {
         this.eventPublisher.publishEvent(new SmsSendFinallyEvent(this, this.getChannelName(), phones, noticeData.getType(), noticeData.getParams(), response));
      }
   }
}
