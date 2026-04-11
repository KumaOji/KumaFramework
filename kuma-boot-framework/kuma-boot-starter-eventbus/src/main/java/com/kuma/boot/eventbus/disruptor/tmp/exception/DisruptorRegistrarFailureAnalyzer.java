package com.kuma.boot.eventbus.disruptor.tmp.exception;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class DisruptorRegistrarFailureAnalyzer extends AbstractFailureAnalyzer<DisruptorRegistrarException> {
   public DisruptorRegistrarFailureAnalyzer() {
   }

   protected FailureAnalysis analyze(Throwable rootFailure, DisruptorRegistrarException cause) {
      return new FailureAnalysis(cause.getMessage(), "\u8bf7\u6dfb\u52a0 basePackages or basePackageClasses", cause);
   }
}
