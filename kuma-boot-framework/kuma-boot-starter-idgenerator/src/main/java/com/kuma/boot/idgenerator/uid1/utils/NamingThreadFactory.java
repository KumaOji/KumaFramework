package com.kuma.boot.idgenerator.uid1.utils;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamingThreadFactory implements ThreadFactory {
   private static final Logger LOGGER = LoggerFactory.getLogger(NamingThreadFactory.class);
   private String name;
   private boolean daemon;
   private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
   private final ConcurrentHashMap<String, AtomicLong> sequences;

   public NamingThreadFactory() {
      this((String)null, false, (Thread.UncaughtExceptionHandler)null);
   }

   public NamingThreadFactory(String name) {
      this(name, false, (Thread.UncaughtExceptionHandler)null);
   }

   public NamingThreadFactory(String name, boolean daemon) {
      this(name, daemon, (Thread.UncaughtExceptionHandler)null);
   }

   public NamingThreadFactory(String name, boolean daemon, Thread.UncaughtExceptionHandler handler) {
      this.name = name;
      this.daemon = daemon;
      this.uncaughtExceptionHandler = handler;
      this.sequences = new ConcurrentHashMap();
   }

   public Thread newThread(Runnable r) {
      Thread thread = new Thread(r);
      thread.setDaemon(this.daemon);
      String prefix = this.name;
      if (StringUtils.isBlank(prefix)) {
         prefix = this.getInvoker(2);
      }

      thread.setName(prefix + "-" + this.getSequence(prefix));
      if (this.uncaughtExceptionHandler != null) {
         thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
      } else {
         thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            {
               Objects.requireNonNull(NamingThreadFactory.this);
            }

            public void uncaughtException(Thread t, Throwable e) {
               NamingThreadFactory.LOGGER.error("unhandled exception in thread: " + t.threadId() + ":" + t.getName(), e);
            }
         });
      }

      return thread;
   }

   private String getInvoker(int depth) {
      Exception e = new Exception();
      StackTraceElement[] stes = e.getStackTrace();
      return stes.length > depth ? ClassUtils.getShortClassName(stes[depth].getClassName()) : this.getClass().getSimpleName();
   }

   private long getSequence(String invoker) {
      AtomicLong r = (AtomicLong)this.sequences.get(invoker);
      if (r == null) {
         r = new AtomicLong(0L);
         AtomicLong previous = (AtomicLong)this.sequences.putIfAbsent(invoker, r);
         if (previous != null) {
            r = previous;
         }
      }

      return r.incrementAndGet();
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isDaemon() {
      return this.daemon;
   }

   public void setDaemon(boolean daemon) {
      this.daemon = daemon;
   }

   public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
      return this.uncaughtExceptionHandler;
   }

   public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
      this.uncaughtExceptionHandler = handler;
   }
}
