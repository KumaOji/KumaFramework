package com.kuma.boot.eventbus.disruptor.tmp3.context;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorBindEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;

public class DisruptorTemplate {
   private Disruptor<DisruptorEvent> disruptor;
   private EventTranslatorOneArg<DisruptorEvent, DisruptorEvent> oneArgEventTranslator;

   public DisruptorTemplate(Disruptor<DisruptorEvent> disruptor, EventTranslatorOneArg<DisruptorEvent, DisruptorEvent> oneArgEventTranslator) {
      this.disruptor = disruptor;
      this.oneArgEventTranslator = oneArgEventTranslator;
   }

   public void publishEvent(DisruptorBindEvent event) {
      this.disruptor.publishEvent(this.oneArgEventTranslator, event);
   }

   public void publishEvent(String event, String tag, Object body) {
      DisruptorBindEvent bindEvent = new DisruptorBindEvent();
      bindEvent.setEvent(event);
      bindEvent.setTag(tag);
      bindEvent.setBody(body);
      this.disruptor.publishEvent(this.oneArgEventTranslator, bindEvent);
   }

   public void publishEvent(String event, String tag, String key, Object body) {
      DisruptorBindEvent bindEvent = new DisruptorBindEvent();
      bindEvent.setEvent(event);
      bindEvent.setTag(tag);
      bindEvent.setKey(key);
      bindEvent.setBody(body);
      this.disruptor.publishEvent(this.oneArgEventTranslator, bindEvent);
   }
}
