package com.kuma.boot.sms.common.loadbalancer;

import com.kuma.boot.sms.common.handler.SendHandler;
import com.kuma.boot.sms.common.model.NoticeData;

public interface SmsSenderLoadBalancer extends ILoadBalancer<SendHandler> {
   default boolean chooseFilter(TargetWrapper<SendHandler> targetWrapper, NoticeData noticeData) {
      return noticeData.getType() != null && targetWrapper.getTarget() != null ? targetWrapper.getTarget().acceptSend(noticeData.getType()) : false;
   }
}
