package com.kuma.boot.eventbus.disruptor.tmp3.translator;

import com.lmax.disruptor.EventTranslatorTwoArg;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public class DisruptorEventTwoArgTranslator implements EventTranslatorTwoArg<DisruptorEvent, String, String> {
   public DisruptorEventTwoArgTranslator() {
   }

   public void translateTo(DisruptorEvent dtEevent, long sequence, String event, String tag) {
      dtEevent.setEvent(event);
      dtEevent.setTag(tag);
      dtEevent.setKey(String.valueOf(sequence));
   }
}
