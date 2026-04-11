package com.kuma.boot.eventbus.disruptor.tmp5.builder;

public class CustomThreadBuilder {
   private String name;
   private Boolean isDaemon;
   private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

   public CustomThreadBuilder() {
      this.isDaemon = Boolean.FALSE;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Boolean getDaemon() {
      return this.isDaemon;
   }

   public void setDaemon(Boolean daemon) {
      this.isDaemon = daemon;
   }

   public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
      return this.uncaughtExceptionHandler;
   }

   public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
      this.uncaughtExceptionHandler = uncaughtExceptionHandler;
   }

   public static InnerrCustomThreadBuilder builder() {
      return new InnerrCustomThreadBuilder();
   }

   public static final class InnerrCustomThreadBuilder {
      private String name;
      private Boolean isDaemon;
      private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

      private InnerrCustomThreadBuilder() {
         this.isDaemon = Boolean.FALSE;
      }

      public InnerrCustomThreadBuilder name(String name) {
         this.name = name;
         return this;
      }

      public InnerrCustomThreadBuilder isDaemon(Boolean isDaemon) {
         this.isDaemon = isDaemon;
         return this;
      }

      public InnerrCustomThreadBuilder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
         this.uncaughtExceptionHandler = uncaughtExceptionHandler;
         return this;
      }

      public CustomThreadBuilder build() {
         CustomThreadBuilder customThreadBuilder = new CustomThreadBuilder();
         customThreadBuilder.setName(this.name);
         customThreadBuilder.setDaemon(this.isDaemon);
         customThreadBuilder.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
         return customThreadBuilder;
      }
   }
}
