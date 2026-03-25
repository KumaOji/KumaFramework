package com.kuma.boot.sms.common.loadbalancer;

import com.kuma.boot.sms.common.handler.SendHandler;
import com.kuma.boot.sms.common.model.NoticeData;

public interface SmsSenderLoadBalancer extends ILoadBalancer {
   default boolean chooseFilter(TargetWrapper targetWrapper, NoticeData noticeData) {
      return noticeData.getType() != null && targetWrapper.getTarget() != null ? ((SendHandler)targetWrapper.getTarget()).acceptSend(noticeData.getType()) : false;
   }
}
