package com.kuma.boot.eventbus.disruptor.tmp4.exeception;

public class AbsentWorkerStation extends RuntimeException {
   public AbsentWorkerStation(String station) {
      super("There is no corresponding WorkerStation " + station);
   }
}
