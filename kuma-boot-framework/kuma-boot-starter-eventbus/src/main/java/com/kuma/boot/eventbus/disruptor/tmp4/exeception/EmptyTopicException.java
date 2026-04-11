package com.kuma.boot.eventbus.disruptor.tmp4.exeception;

public class EmptyTopicException extends RuntimeException {
   public EmptyTopicException() {
      super("The topic cannot be an empty string");
   }
}
