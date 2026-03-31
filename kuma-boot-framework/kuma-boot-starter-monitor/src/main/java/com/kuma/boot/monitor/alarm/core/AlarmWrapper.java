package com.kuma.boot.monitor.alarm.core;

import com.kuma.boot.monitor.alarm.core.helper.ExecuteHelper;
import com.kuma.boot.monitor.alarm.core.loader.ConfLoaderFactory;
import com.kuma.boot.monitor.alarm.core.loader.api.IConfLoader;
import com.kuma.boot.monitor.alarm.core.util.IpUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmWrapper {
   private static final Logger logger = LoggerFactory.getLogger(AlarmWrapper.class);
   private ExecutorService alarmExecutorService;
   private final ConcurrentHashMap<String, AtomicInteger> alarmCountMap = new ConcurrentHashMap();
   private final IConfLoader confLoader = ConfLoaderFactory.loader();

   public static AlarmWrapper getInstance() {
      return AlarmWrapper.InnerInstance.instance;
   }

   private AlarmWrapper() {
      this.initExecutorService();
   }

   public void initExecutorService() {
      this.alarmExecutorService = new ThreadPoolExecutor(3, 5, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque(10), new DefaultThreadFactory("kmc-monitor-sms-sender"), new ThreadPoolExecutor.CallerRunsPolicy());
      ScheduledExecutorService scheduleExecutorService = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("kmc-monitor-clear-sender"));
      scheduleExecutorService.scheduleAtFixedRate(() -> {
         for(Map.Entry<String, AtomicInteger> entry : this.alarmCountMap.entrySet()) {
            ((AtomicInteger)entry.getValue()).set(0);
         }

      }, 0L, 1L, TimeUnit.MINUTES);
   }

   public void sendMsg(String key, String content) {
      this.sendMsg(new AlarmContent(key, (String)null, content, (List)null, " ip:{0} >>> key:{1} >>> \u5f02\u5e38\u6570:{2} >>> {3}"));
   }

   public void sendMsg(String key, String title, String content) {
      this.sendMsg(new AlarmContent(key, title, content, (List)null, " ip:{0} >>> key:{1} >>> \u5f02\u5e38\u6570:{2} >>> {3}"));
   }

   public void sendMsg(String key, String title, String content, String template) {
      this.sendMsg(new AlarmContent(key, title, content, (List)null, template));
   }

   public void sendMsgToUser(String key, String content, List<String> users) {
      this.sendMsg(new AlarmContent(key, (String)null, content, users, " ip:{0} >>> key:{1} >>> \u5f02\u5e38\u6570:{2} >>> {3}"));
   }

   public void sendMsgToUser(String key, String title, String content, List<String> users) {
      this.sendMsg(new AlarmContent(key, title, content, users, " ip:{0} >>> key:{1} >>> \u5f02\u5e38\u6570:{2} >>> {3}"));
   }

   public void sendMsgToUser(String key, String title, String content, String template, List<String> users) {
      this.sendMsg(new AlarmContent(key, title, content, users, template));
   }

   private void sendMsg(AlarmContent alarmContent) {
      try {
         int count = this.getAlarmCount(alarmContent.key);
         alarmContent.setCount(count);
         List<ExecuteHelper> executeHelper = this.confLoader.getExecuteHelper(alarmContent.key, count);
         executeHelper.forEach((executeHelper1) -> this.doSend(executeHelper1, alarmContent));
      } catch (Exception e) {
         logger.error("AlarmWrapper.sendMsg error! content:{}, e:{}", alarmContent, e);
      }

   }

   private void doSend(final ExecuteHelper executeHelper, final AlarmContent alarmContent) {
      this.alarmExecutorService.execute(() -> executeHelper.getIExecute().sendMsg((List)Optional.ofNullable(alarmContent.getAlarmUser()).orElse(executeHelper.getUsers()), alarmContent.getTitle(), alarmContent.getContent()));
   }

   private int getAlarmCount(String key) {
      if (!this.alarmCountMap.containsKey(key)) {
         synchronized(this) {
            if (!this.alarmCountMap.containsKey(key)) {
               this.alarmCountMap.put(key, new AtomicInteger(0));
            }
         }
      }

      return ((AtomicInteger)this.alarmCountMap.get(key)).addAndGet(1);
   }

   private static class InnerInstance {
      static AlarmWrapper instance = new AlarmWrapper();

      private InnerInstance() {
      }
   }

   private static class AlarmContent {
      static final String DEFAULT_CONTENT_TEMPLATE = " ip:{0} >>> key:{1} >>> \u5f02\u5e38\u6570:{2} >>> {3}";
      private static String LOCAL_IP = IpUtil.getLocalIp();
      private static String PREFIX;
      private String key;
      private String title;
      private String content;
      private int count;
      private List<String> alarmUser;
      private String template;

      public AlarmContent(String key, String title, String content, List<String> alarmUser, String template) {
         this.key = key;
         this.title = title;
         this.content = content;
         this.alarmUser = alarmUser;
         this.template = (String)Optional.ofNullable(template).orElse(" ip:{0} >>> key:{1} >>> \u5f02\u5e38\u6570:{2} >>> {3}");
      }

      public String getTitle() {
         return this.title == null ? PREFIX : this.title;
      }

      public void setCount(int count) {
         this.count = count;
      }

      public String getContent() {
         return MessageFormat.format(this.template, LOCAL_IP, this.key, this.count, this.content);
      }

      public List<String> getAlarmUser() {
         return this.alarmUser != null && !this.alarmUser.isEmpty() ? this.alarmUser : null;
      }

      public String toString() {
         String var10000 = this.key;
         return "AlarmContent{key='" + var10000 + "', title='" + this.title + "', content='" + this.content + "', count=" + this.count + ", alarmUser=" + String.valueOf(this.alarmUser) + ", template='" + this.template + "'}";
      }

      public static String getLocalIp() {
         return LOCAL_IP;
      }

      public static void setLocalIp(String localIp) {
         LOCAL_IP = localIp;
      }

      public static String getPREFIX() {
         return PREFIX;
      }

      public static void setPREFIX(String PREFIX) {
         AlarmWrapper.AlarmContent.PREFIX = PREFIX;
      }

      public String getKey() {
         return this.key;
      }

      public void setKey(String key) {
         this.key = key;
      }

      public void setTitle(String title) {
         this.title = title;
      }

      public void setContent(String content) {
         this.content = content;
      }

      public int getCount() {
         return this.count;
      }

      public void setAlarmUser(List<String> alarmUser) {
         this.alarmUser = alarmUser;
      }

      public String getTemplate() {
         return this.template;
      }

      public void setTemplate(String template) {
         this.template = template;
      }

      static {
         try {
            PREFIX = "[" + ConfLoaderFactory.loader().getRegisterInfo().getAppName() + "]";
         } catch (Exception var1) {
            PREFIX = "[\u62a5\u8b66]";
         }

      }
   }
}
