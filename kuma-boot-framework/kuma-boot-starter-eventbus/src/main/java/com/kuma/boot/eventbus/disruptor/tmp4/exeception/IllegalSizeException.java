package com.kuma.boot.eventbus.disruptor.tmp4.exeception;

public class IllegalSizeException extends RuntimeException {
   public IllegalSizeException(String line) {
      super("The size of the event line \"" + line + "\" should greater (>) than 0");
   }
}
