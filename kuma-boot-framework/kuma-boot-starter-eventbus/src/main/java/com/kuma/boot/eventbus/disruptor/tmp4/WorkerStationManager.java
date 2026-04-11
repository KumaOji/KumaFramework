package com.kuma.boot.eventbus.disruptor.tmp4;

import com.lmax.disruptor.EventHandler;
import com.kuma.boot.eventbus.disruptor.tmp4.event.DistributeEvent;
import com.kuma.boot.eventbus.disruptor.tmp4.thread.SafeThreadPool;
import java.util.concurrent.ConcurrentHashMap;

public enum WorkerStationManager implements EventHandler<DistributeEvent> {
   INSTANCE;

   private final ConcurrentHashMap<String, WorkerStation> workerStations = new ConcurrentHashMap();
   private final ConcurrentHashMap<String, SafeThreadPool> threadPools = new ConcurrentHashMap();

   private WorkerStationManager() {
   }

   public void register(String name, WorkerStation line) {
      this.workerStations.put(name, line);
   }

   public void registerThreadPool(String station, SafeThreadPool threadPool) {
      this.threadPools.put(station, threadPool);
   }

   public WorkerStation getWorkerStation(String name) {
      return (WorkerStation)this.workerStations.get(name);
   }

   public void stopAll() {
      this.workerStations.forEach((k, v) -> v.stop());
      this.threadPools.forEach((k, v) -> v.shotDown());
   }

   public void onEvent(DistributeEvent event, long l, boolean b) throws Exception {
      try {
         WorkerStation workerStation = this.getWorkerStation(event.getTargetStation());
         workerStation.publish((e, s) -> {
            while(!e.isAvailable() || !e.doing()) {
            }

            e.setContext(event.getContext());
            e.setListeners(workerStation.getEventListeners(event.getTargetTopic()));
         });
      } finally {
         event.finished();
      }

   }

   // $FF: synthetic method
   private static WorkerStationManager[] $values() {
      return new WorkerStationManager[]{INSTANCE};
   }
}
