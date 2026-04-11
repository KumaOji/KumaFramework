package com.kuma.boot.eventbus.disruptor.tmp4.autoconfigure;

import com.lmax.disruptor.EventTranslator;
import com.kuma.boot.eventbus.disruptor.tmp4.Distributor;
import com.kuma.boot.eventbus.disruptor.tmp4.DistributorLine;
import com.kuma.boot.eventbus.disruptor.tmp4.WorkerStation;
import com.kuma.boot.eventbus.disruptor.tmp4.WorkerStationManager;
import com.kuma.boot.eventbus.disruptor.tmp4.anno.StationEventListener;
import com.kuma.boot.eventbus.disruptor.tmp4.event.DistributeEvent;
import com.kuma.boot.eventbus.disruptor.tmp4.exeception.AbsentWorkerStation;
import com.kuma.boot.eventbus.disruptor.tmp4.exeception.EmptyTopicException;
import com.kuma.boot.eventbus.disruptor.tmp4.exeception.IllegalSizeException;
import com.kuma.boot.eventbus.disruptor.tmp4.listener.WorkerEventListener;
import com.kuma.boot.eventbus.disruptor.tmp4.thread.DelayEventQueue;
import com.kuma.boot.eventbus.disruptor.tmp4.util.CglibUtils;
import com.kuma.boot.eventbus.disruptor.tmp4.util.NumberUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

@Configuration
@EnableConfigurationProperties({WorkerStationConfig.class, DistributorLineConfig.class})
public class DistributorAutoConfigure implements ApplicationListener<ContextRefreshedEvent> {
   public DistributorAutoConfigure() {
   }

   public void onApplicationEvent(ContextRefreshedEvent event) {
      ApplicationContext applicationContext = event.getApplicationContext();
      Map<String, Object> eventListenerMap = applicationContext.getBeansWithAnnotation(StationEventListener.class);
      WorkerStationManager workerLineManager = WorkerStationManager.INSTANCE;

      for(Object eventListener : eventListenerMap.values()) {
         if (eventListener instanceof WorkerEventListener) {
            Class<?> realClazz = CglibUtils.filterCglibProxyClass(eventListener.getClass());
            StationEventListener listener = (StationEventListener)realClazz.getAnnotation(StationEventListener.class);
            String station = listener.station();
            if (!station.isEmpty()) {
               WorkerStation workerStation = workerLineManager.getWorkerStation(station);
               if (workerStation == null) {
                  throw new AbsentWorkerStation(station);
               }

               String topic = listener.topic();
               if (Objects.equals(topic, "")) {
                  throw new EmptyTopicException();
               }

               workerStation.register(topic, (WorkerEventListener)eventListener);
            }
         }
      }

   }

   @Bean
   @ConditionalOnMissingBean
   public Distributor distributor(DistributorLineConfig distributorLineConfig, WorkerStationConfig workerLineConfig) {
      WorkerStationManager workerLineManager = WorkerStationManager.INSTANCE;
      workerLineConfig.getWorkerStations().forEach((config) -> workerLineManager.register(config.getStation(), new WorkerStation(config)));
      int num = distributorLineConfig.getHandlerNum();
      ArrayList<DistributorLine> distributorLines = new ArrayList(num);
      int lineConfigSize = distributorLineConfig.getSize();
      String name = distributorLineConfig.getName();
      if (lineConfigSize < 1) {
         throw new IllegalSizeException(name);
      } else {
         int size = NumberUtil.upper2Pow(lineConfigSize / num);
         DelayEventQueue<EventTranslator<DistributeEvent>> delayEventQueue = new DelayEventQueue<EventTranslator<DistributeEvent>>();

         for(int i = 0; i < num; ++i) {
            distributorLines.add(new DistributorLine(name, size, delayEventQueue));
         }

         return (new Distributor(name, distributorLines)).start();
      }
   }
}
