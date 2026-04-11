package com.kuma.boot.eventbus;

import com.google.common.eventbus.Subscribe;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.model.EventModel;
import org.greenrobot.eventbus.ThreadMode;

public abstract class ExecutableEventListener extends BaseEventListener<EventModel<?>> {
   public ExecutableEventListener() {
   }

   @org.springframework.context.event.EventListener
   @Subscribe
   @org.greenrobot.eventbus.Subscribe(
      threadMode = ThreadMode.ASYNC
   )
   public void onMessage(EventModel<?> message) {
      LogUtils.info("\u6536\u5230\u6d88\u606f\uff1a{}", new Object[]{message});
      if (this.topic().equals(message.getTopic())) {
         this.handle(message);
      }

   }

   public void onEvent(EventModel<?> event, long sequence, boolean endOfBatch) throws Exception {
      this.onMessage(event);
   }

   protected abstract void handle(EventModel<?> message);
}
