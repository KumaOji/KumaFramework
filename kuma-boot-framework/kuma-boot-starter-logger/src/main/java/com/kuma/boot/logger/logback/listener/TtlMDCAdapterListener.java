package com.kuma.boot.logger.logback.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.util.Objects;
import org.slf4j.TtlMDCAdapter;

public class TtlMDCAdapterListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
   public TtlMDCAdapterListener() {
   }

   public void start() {
      TtlMDCAdapter.getInstance();
      Context context = this.getContext();
      if (Objects.nonNull(context)) {
         context.putProperty("RPC_PORT", System.getProperty("rpc_port", "10000"));
      }

   }

   public void stop() {
   }

   public boolean isStarted() {
      return false;
   }

   public boolean isResetResistant() {
      return false;
   }

   public void onStart(LoggerContext context) {
   }

   public void onReset(LoggerContext context) {
   }

   public void onStop(LoggerContext context) {
   }

   public void onLevelChange(Logger logger, Level level) {
   }
}
