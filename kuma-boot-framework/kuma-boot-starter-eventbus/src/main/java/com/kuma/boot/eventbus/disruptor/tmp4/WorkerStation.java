package com.kuma.boot.eventbus.disruptor.tmp4;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.eventbus.disruptor.tmp4.autoconfigure.WorkerStationConfig;
import com.kuma.boot.eventbus.disruptor.tmp4.event.WorkEvent;
import com.kuma.boot.eventbus.disruptor.tmp4.exeception.IllegalSizeException;
import com.kuma.boot.eventbus.disruptor.tmp4.listener.WorkerEventListener;
import com.kuma.boot.eventbus.disruptor.tmp4.thread.SafeThreadPool;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerStation {
   private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStation.class);
   private final String name;
   private final Disruptor<WorkEvent> workerLine;
   private final ConcurrentHashMap<String, HashSet<WorkerEventListener>> listeners = new ConcurrentHashMap();

   public WorkerStation(WorkerStationConfig.Config config) {
      this.name = config.getStation();
      int size = config.getLineSize();
      if (size < 1) {
         throw new IllegalSizeException(this.name);
      } else {
         this.workerLine = new Disruptor(WorkEvent::new, size, (r) -> {
            Thread thread = new Thread(r);
            thread.setName(this.name);
            thread.setDaemon(true);
            return thread;
         }, ProducerType.MULTI, new YieldingWaitStrategy());
         SafeThreadPool threadPool = new SafeThreadPool(this.name, config.getHandlerNum(), config.getKeepAliveTime());
         WorkerStationManager.INSTANCE.registerThreadPool(this.name, threadPool);
         this.workerLine.handleEventsWith(new EventHandler[]{new WorkerLineHandler(threadPool)});
         this.workerLine.start();
         LOGGER.info("\u3010WorkerStation\u3011 \"{}\" start to work", this.name);
      }
   }

   public boolean register(String topic, WorkerEventListener listener) {
      HashSet<WorkerEventListener> topicListeners = (HashSet)this.listeners.get(topic);
      if (topicListeners == null) {
         topicListeners = new HashSet(Collections.singleton(listener));
         this.listeners.put(topic, topicListeners);
         return true;
      } else if (topicListeners.contains(listener)) {
         return false;
      } else {
         topicListeners.add(listener);
         return true;
      }
   }

   public Set<WorkerEventListener> getEventListeners(String topic) {
      return (Set)this.listeners.get(topic);
   }

   public void publish(EventTranslator<WorkEvent> translator) {
      this.workerLine.getRingBuffer().publishEvent(translator);
   }

   public void stop() {
      this.workerLine.halt();
      LOGGER.info("\u3010WorkerStation\u3011 \"{}\" has stopped", this.name);
   }
}
