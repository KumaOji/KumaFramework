package com.kuma.boot.eventbus.disruptor.tmp.exception;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class DisruptorRegistrarFailureAnalyzer extends AbstractFailureAnalyzer<DisruptorRegistrarException> {
   public DisruptorRegistrarFailureAnalyzer() {
   }

   protected FailureAnalysis analyze(Throwable rootFailure, DisruptorRegistrarException cause) {
      return new FailureAnalysis(cause.getMessage(), "请添加 basePackages or basePackageClasses", cause);
   }
}
