package com.kuma.boot.monitor.warn;

import com.kuma.boot.monitor.model.Message;

public abstract class AbstractWarn {
   public AbstractWarn() {
   }

   public abstract void notify(Message message);
}
