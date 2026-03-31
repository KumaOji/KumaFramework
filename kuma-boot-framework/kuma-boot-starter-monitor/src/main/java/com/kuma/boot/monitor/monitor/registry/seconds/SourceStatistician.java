package com.kuma.boot.monitor.monitor.registry.seconds;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.monitor.monitor.Monitor;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SourceStatistician {
   @Value("${seconds.monitor.size:3}")
   private int size = 3;
   @Value("${seconds.monitor.send.seconds:6}")
   private int sendSeconds;
   @Value("${seconds.monitor.machine.millis:60000}")
   private int machineMillis;
   @Value("${seconds.monitor.url:}")
   private String monitorUrl;
   private int n;
   private AtomicReferenceArray<SecondData> secondDataLinkedList;
   private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("monitor-thread").build());

   public SourceStatistician() {
      this.secondDataLinkedList = new AtomicReferenceArray<>((int) Math.pow(2.0, this.size));
   }

   @PostConstruct
   public void init() {
      this.secondDataLinkedList = new AtomicReferenceArray<>((int) Math.pow(2.0, this.size));
      this.n = (int) Math.pow(2.0, this.size) - 1;
      executorService.scheduleWithFixedDelay(() -> {
         Monitor.TimeContext context = Monitor.timer("seconds_monitor_send");

         try {
            if (this.monitorUrl != null && !this.monitorUrl.isEmpty()) {
               for(String s : this.createInfluxData()) {
                  this.sendHttp(s);
               }

               return;
            }
         } catch (Exception e) {
            LogUtils.error(e, "createInfluxData error");
            return;
         } finally {
            context.end();
         }

      }, this.sendSeconds, this.sendSeconds, TimeUnit.SECONDS);
   }

   public void record(String key, String tag, double count, long totalTime) {
      try {
         SecondData secondData = this.createOrGet();
         SourceData sourceData = secondData.get(key, tag);
         if (sourceData == null) {
            sourceData = secondData.put(key, tag, new SourceData());
         }

         sourceData.record(count, totalTime);
      } catch (Exception e) {
         LogUtils.error(e, "record error");
      }

   }

   private SecondData createOrGet() {
      long nowTime = System.currentTimeMillis() / 1000L;
      int index = (int)(nowTime & (long)this.n);

      SecondData currentSecondData;
      for (currentSecondData = this.secondDataLinkedList.get(index);
           currentSecondData == null || currentSecondData.getTime() != nowTime;
           currentSecondData = this.secondDataLinkedList.get(index)) {
         SecondData tmp = new SecondData(nowTime);
         this.secondDataLinkedList.compareAndSet(index, currentSecondData, tmp);
      }

      return currentSecondData;
   }

   public AtomicReferenceArray<SecondData> pull() {
      return this.secondDataLinkedList;
   }

   public void sendHttp(String data) {
      try {
         HttpURLConnection conn = (HttpURLConnection) URI.create(this.monitorUrl).toURL().openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
         conn.setDoOutput(true);
         try (OutputStream os = conn.getOutputStream()) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
         }
         conn.getResponseCode();
         try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
         }
         conn.disconnect();
      } catch (Exception e) {
         LogUtils.error(e, "sendHttp error, data:{}", data);
      }
   }

   public List<String> createInfluxData() {
      long secondTime = System.currentTimeMillis() / 1000L;
      List<String> list = new ArrayList<>(this.secondDataLinkedList.length());
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < this.secondDataLinkedList.length(); ++i) {
         SecondData secondData = this.secondDataLinkedList.get(i);
         if (secondData != null && secondTime != secondData.getTime()) {
            for (Map.Entry<String, SourceData> entry : secondData.getData().entrySet()) {
               this.append(sb, entry.getKey(), null, secondData, entry.getValue());
            }

            for (Map.Entry<String, ConcurrentHashMap<String, SourceData>> entry : secondData.getTagData().entrySet()) {
               for (Map.Entry<String, SourceData> entry1 : entry.getValue().entrySet()) {
                  this.append(sb, entry.getKey(), entry1.getKey(), secondData, entry1.getValue());
               }
            }

            list.add(sb.toString());
            sb.setLength(0);
         }
      }

      return list;
   }

   private void append(StringBuilder sb, String key, String tag, SecondData secondData, SourceData sourceData) {
      sb.append(key).append(",").append("host=").append(Monitor.IP);
      if (tag != null) {
         sb.append(",").append("tag=").append(tag);
      }

      sb.append(" ").append("tps=").append(sourceData.getCounter());
      long max = sourceData.getMax();
      if (max > 0L) {
         sb.append(",").append("tp99=").append(sourceData.getTP99()).append(",").append("tp90=").append(sourceData.getTP90()).append(",").append("tp50=").append(sourceData.getTP50()).append(",").append("max=").append(sourceData.getMax());
      }

      sb.append(" ").append(TimeUnit.SECONDS.toNanos(secondData.getTime())).append("\n");
   }

   public static void main(String[] args) {
      SourceStatistician sourceStatistician = new SourceStatistician();
      sourceStatistician.monitorUrl = "http://10.48.2.15:8086/write?db=monitor";
      String s = "seconds_system_cpu_used,host=10.205.9.121 tps=10,tp99=10,tp90=10,tp50=5,max=10 1733986663000000000\n";
      sourceStatistician.sendHttp(s);
      sourceStatistician.sendHttp(s);
   }
}
