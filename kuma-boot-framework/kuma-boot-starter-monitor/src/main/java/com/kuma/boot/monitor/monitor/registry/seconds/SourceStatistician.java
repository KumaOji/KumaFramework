package com.kuma.boot.monitor.monitor.registry.seconds;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.monitor.monitor.Monitor;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
      this.secondDataLinkedList = new AtomicReferenceArray((int)Math.pow((double)2.0F, (double)this.size));
   }

   @PostConstruct
   public void init() {
      this.secondDataLinkedList = new AtomicReferenceArray((int)Math.pow((double)2.0F, (double)this.size));
      this.n = (int)Math.pow((double)2.0F, (double)this.size) - 1;
      executorService.scheduleWithFixedDelay(() -> {
         Monitor.TimeContext context = Monitor.timer("seconds_monitor_send");

         try {
            if (this.monitorUrl != null && !this.monitorUrl.equals("")) {
               for(String s : this.createInfluxData()) {
                  this.sendHttp(s);
               }

               return;
            }
         } catch (Exception e) {
            LogUtils.error("createInfluxData error:", new Object[]{e});
            return;
         } finally {
            context.end();
         }

      }, (long)this.sendSeconds, (long)this.sendSeconds, TimeUnit.SECONDS);
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
         LogUtils.error("record error.request:", new Object[]{e});
      }

   }

   private SecondData createOrGet() {
      long nowTime = System.currentTimeMillis() / 1000L;
      int index = (int)(nowTime & (long)this.n);

      SecondData currentSecondData;
      for(currentSecondData = (SecondData)this.secondDataLinkedList.get(index); currentSecondData == null || currentSecondData.getTime() != nowTime; currentSecondData = (SecondData)this.secondDataLinkedList.get(index)) {
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
         URL url = new URL(this.monitorUrl);
         HttpURLConnection conn = (HttpURLConnection)url.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
         conn.setDoOutput(true);
         OutputStream os = conn.getOutputStream();

         try {
            os.write(data.getBytes("UTF-8"));
            os.flush();
         } catch (Throwable var10) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (os != null) {
            os.close();
         }

         int responseCode = conn.getResponseCode();
         BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

         try {
            StringBuilder response = new StringBuilder();

            String inputLine;
            while((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
         } catch (Throwable var11) {
            try {
               in.close();
            } catch (Throwable var8) {
               var11.addSuppressed(var8);
            }

            throw var11;
         }

         in.close();
         conn.disconnect();
      } catch (Exception e) {
         LogUtils.error("record error data:{}", new Object[]{data, e});
      }

   }

   public List<String> createInfluxData() {
      Long secondTime = System.currentTimeMillis() / 1000L;
      List<String> list = new ArrayList(this.secondDataLinkedList.length());
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < this.secondDataLinkedList.length(); ++i) {
         SecondData secondData = (SecondData)this.secondDataLinkedList.get(i);
         if (secondData != null && secondTime != secondData.getTime()) {
            for(Map.Entry<String, SourceData> entry : secondData.getData().entrySet()) {
               String key = (String)entry.getKey();
               SourceData sourceData = (SourceData)entry.getValue();
               this.append(sb, key, (String)null, secondData, sourceData);
            }

            for(Map.Entry<String, ConcurrentHashMap<String, SourceData>> entry : secondData.getTagData().entrySet()) {
               String key = (String)entry.getKey();

               for(Map.Entry<String, SourceData> entry1 : ((ConcurrentHashMap)entry.getValue()).entrySet()) {
                  String tag = (String)entry1.getKey();
                  SourceData sourceData = (SourceData)entry1.getValue();
                  this.append(sb, key, tag, secondData, sourceData);
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
