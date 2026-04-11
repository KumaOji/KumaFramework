package com.kuma.boot.eventbus.disruptor.tmp3.properties;

import com.kuma.boot.eventbus.disruptor.tmp3.context.EventHandlerDefinition;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(
   prefix = "kuma.boot.eventbus.disruptor"
)
public class DisruptorProperties {
   public static final String PREFIX = "kuma.boot.eventbus.disruptor";
   private boolean enabled = false;
   private boolean ringBuffer = false;
   private int ringBufferSize = 1024;
   private int ringThreadNumbers = 4;
   private boolean multiProducer = false;
   private List<EventHandlerDefinition> handlerDefinitions = new ArrayList();

   public DisruptorProperties() {
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean getRingBuffer() {
      return this.ringBuffer;
   }

   public void setRingBuffer(boolean ringBuffer) {
      this.ringBuffer = ringBuffer;
   }

   public boolean getMultiProducer() {
      return this.multiProducer;
   }

   public void setMultiProducer(boolean multiProducer) {
      this.multiProducer = multiProducer;
   }

   public int getRingBufferSize() {
      return this.ringBufferSize;
   }

   public void setRingBufferSize(int ringBufferSize) {
      this.ringBufferSize = ringBufferSize;
   }

   public int getRingThreadNumbers() {
      return this.ringThreadNumbers;
   }

   public void setRingThreadNumbers(int ringThreadNumbers) {
      this.ringThreadNumbers = ringThreadNumbers;
   }

   public List<EventHandlerDefinition> getHandlerDefinitions() {
      return this.handlerDefinitions;
   }

   public void setHandlerDefinitions(List<EventHandlerDefinition> handlerDefinitions) {
      this.handlerDefinitions = handlerDefinitions;
   }
}
