package com.kuma.boot.sms.common.loadbalancer;

import com.kuma.boot.sms.common.handler.SendHandler;
import com.kuma.boot.sms.common.model.NoticeData;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class WeightRandomSmsLoadBalancer extends WeightRandomLoadBalancer<SendHandler> implements SmsSenderLoadBalancer {
   public static final String TYPE_NAME = "WeightRandom";

   @Override
   protected @Nullable SendHandler choose0(List<TargetWrapper<SendHandler>> activeTargetList, Object chooseReferenceObject) {
      NoticeData noticeData = (NoticeData) chooseReferenceObject;
      List<TargetWrapper<SendHandler>> newActiveTargetList = activeTargetList.stream().filter((wrapper) -> this.chooseFilter(wrapper, noticeData)).toList();
      return newActiveTargetList.isEmpty() ? null : super.choose0(newActiveTargetList, chooseReferenceObject);
   }
}
