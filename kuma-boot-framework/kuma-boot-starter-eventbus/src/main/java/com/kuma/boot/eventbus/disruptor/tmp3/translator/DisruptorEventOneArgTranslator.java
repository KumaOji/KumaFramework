package com.kuma.boot.eventbus.disruptor.tmp3.translator;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorBindEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.util.StringUtils;

public class DisruptorEventOneArgTranslator implements EventTranslatorOneArg<DisruptorEvent, DisruptorEvent> {
   public DisruptorEventOneArgTranslator() {
   }

   public void translateTo(DisruptorEvent event, long sequence, DisruptorEvent bind) {
      event.setSource(bind.getSource());
      event.setEvent(bind.getEvent());
      event.setTag(bind.getTag());
      event.setKey(StringUtils.hasText(bind.getKey()) ? bind.getKey() : String.valueOf(sequence));
      if (event instanceof DisruptorBindEvent bindEvent) {
         bindEvent.bind(bind);
      }

   }
}
