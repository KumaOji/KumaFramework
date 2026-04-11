package com.kuma.boot.eventbus.disruptor.tmp3.translator;

import com.lmax.disruptor.EventTranslatorThreeArg;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public class DisruptorEventThreeArgTranslator implements EventTranslatorThreeArg<DisruptorEvent, String, String, String> {
   public DisruptorEventThreeArgTranslator() {
   }

   public void translateTo(DisruptorEvent dtEevent, long sequence, String event, String tag, String key) {
      dtEevent.setEvent(event);
      dtEevent.setTag(tag);
      dtEevent.setKey(key);
   }
}
