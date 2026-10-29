package com.kuma.boot.monitor.warn;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.core.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.servlet.RequestUtils;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.properties.WarnProperties;
import com.kuma.boot.monitor.enums.WarnTypeEnum;
import com.kuma.boot.monitor.model.Message;
import com.kuma.boot.monitor.utils.ExceptionUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class WarnProvider extends AbstractWarn implements AutoCloseable, ApplicationRunner {
   private boolean isClose;
   private final Monitor monitorThreadPool;
   private final WarnProperties warnProperties;
   private final DuplicateFilter duplicateFilter;
   private final Object lock = new Object();
   private final List<AbstractWarn> warns = new ArrayList();
   private final ConcurrentLinkedDeque<Message> messages = new ConcurrentLinkedDeque();
   private final AtomicInteger atomicInteger = new AtomicInteger(0);
   private final AtomicBoolean atomicChannel = new AtomicBoolean(false);

   public WarnProvider(WarnProperties warnProperties, Monitor monitorThreadPool) {
      this.warnProperties = warnProperties;
      this.monitorThreadPool = monitorThreadPool;
      this.duplicateFilter = new DuplicateFilter(warnProperties);
      this.isClose = false;
      this.registerWarn();
      this.monitorThreadPool.monitorSubmit("系统任务: WarnProvider 实时报警任务", (Runnable)(() -> {
         while(!this.monitorThreadPool.monitorIsShutdown() && !this.isClose) {
            try {
               this.notifyRunning();
            } catch (Exception var4) {
               LogUtils.warn("kuma-boot-starter-monitor", new Object[]{"WarnProvider 消息循环异常"});
            }

            try {
               Thread.sleep((long)warnProperties.getTimeSpan() * 1000L);
            } catch (Exception e) {
               LogUtils.error(e);
            }
         }

      }));
   }

   public void registerWarn() {
      if (this.warnProperties.isDingDingWarnEnabled()) {
         this.warns.add(new DingdingWarn());
      }

      if (this.warnProperties.getEmailWarnEnabled()) {
         this.warns.add(new MailWarn());
      }

      if (this.warnProperties.getSmsWarnEnabled()) {
         this.warns.add(new SmsWarn());
      }

      this.warns.add(new LoggerWarn());
   }

   public void clearWarn() {
      this.warns.clear();
   }

   private void notifyRunning() {
      Message msg = new Message();
      msg.setWarnType(WarnTypeEnum.WARN);
      List<Message> msgs = this.getAllMessage();
      int msgCount = this.atomicInteger.getAndSet(0);
      if (msgCount > 0) {
         StringBuilder content = new StringBuilder();
         content.append(String.format("最新报警累计:%s条,详情请查看日志系统,最后%s条报警内容如下:\n", msgCount, this.warnProperties.getCacheCount()));
         msgs.forEach((c) -> {
            if (c.getWarnType().getLevel() > msg.getWarnType().getLevel()) {
               msg.setWarnType(c.getWarnType());
            }

            content.append(String.format("[%s][%s]内容%s\n", c.getWarnType().getDescription(), c.getTitle(), c.getContent()));
         });
         msg.setTitle(String.format("收到%s条报警", msgCount));
         msg.setContent(content.toString());
         this.notifyNow(msg);
      }

   }

   public void notify(Message message) {
      this.addMessage(message);
   }

   public void notify(String warnType, String title, String content) {
      Message message = new Message(WarnTypeEnum.valueOf(warnType), title, content);
      this.addMessage(message);
   }

   private void addMessage(Message msg) {
      this.atomicInteger.getAndIncrement();
      synchronized(this.lock) {
         this.messages.add(msg);
         if (this.messages.size() > this.warnProperties.getCacheCount()) {
            int cacheCount = this.warnProperties.getCacheCount();

            for(int i = 0; i < this.messages.size() - cacheCount; ++i) {
               if (!this.messages.isEmpty()) {
                  this.messages.removeFirst();
               }
            }
         }

      }
   }

   private List<Message> getAllMessage() {
      List<Message> msgs = new ArrayList(this.messages.size());
      synchronized(this.lock) {
         msgs.addAll(this.messages);
         this.messages.clear();
         return msgs;
      }
   }

   public void notifyNow(Message message) {
      this.notifyMessage0(message);
   }

   public void notifyNow(String warnType, String title, String content) {
      Message message = new Message(WarnTypeEnum.valueOf(warnType), title, content);
      this.notifyMessage0(message);
   }

   private void notifyMessage0(Message message) {
      if (!this.duplicateFilter.ifDuplicate(message.getContent()) && this.atomicChannel.get()) {
         if (WarnTypeEnum.ERROR == message.getWarnType()) {
            ExceptionUtils.reportException(message);
         }

         for(AbstractWarn warn : this.warns) {
            message.setTitle(String.format("[%s][%s][%s][%s]%s", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()), RequestUtils.getIpAddress(), PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY), PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY), StringUtils.nullToEmpty(message.getTitle())));
            warn.notify(message);
         }
      }

   }

   public void close() {
      this.isClose = true;
   }

   public void run(ApplicationArguments args) {
      this.atomicChannel.getAndSet(true);
      LogUtils.info("kuma-boot-starter-monitor", new Object[]{"开启消息通道"});
   }

   private static class DuplicateFilter {
      private WarnProperties warnProperties;
      private int cacheMax = 100;
      private volatile List<Integer> cacheTag;
      private long lastClearTime;

      public DuplicateFilter(WarnProperties warnProperties) {
         this.cacheTag = new ArrayList(this.cacheMax + 5);
         this.lastClearTime = System.currentTimeMillis();
         this.warnProperties = warnProperties;
      }

      public boolean ifDuplicate(String message) {
         int hash = StringUtils.nullToEmpty(message).replaceAll("\\d+", "").hashCode();
         if (System.currentTimeMillis() - this.lastClearTime > TimeUnit.MINUTES.toMillis((long)this.warnProperties.getDuplicateTimeSpan())) {
            this.cacheTag.clear();
            this.lastClearTime = System.currentTimeMillis();
         }

         if (this.cacheTag.size() >= this.cacheMax) {
            this.cacheTag.clear();
         }

         if (!this.cacheTag.contains(hash)) {
            this.cacheTag.add(hash);
            return false;
         } else {
            return true;
         }
      }
   }
}
