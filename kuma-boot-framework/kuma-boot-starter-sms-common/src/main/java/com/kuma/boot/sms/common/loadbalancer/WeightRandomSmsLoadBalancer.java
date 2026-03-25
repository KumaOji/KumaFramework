package com.kuma.boot.sms.common.loadbalancer;

import com.kuma.boot.sms.common.handler.SendHandler;
import com.kuma.boot.sms.common.model.NoticeData;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class WeightRandomSmsLoadBalancer extends WeightRandomLoadBalancer implements SmsSenderLoadBalancer {
   public static final String TYPE_NAME = "WeightRandom";

   protected @Nullable SendHandler choose0(List activeTargetList, NoticeData chooseReferenceObject) {
      List<TargetWrapper<SendHandler>> newActiveTargetList = activeTargetList.stream().filter((wrapper) -> this.chooseFilter(wrapper, chooseReferenceObject)).toList();
      return newActiveTargetList.isEmpty() ? null : (SendHandler)super.choose0(activeTargetList, chooseReferenceObject);
   }
}
