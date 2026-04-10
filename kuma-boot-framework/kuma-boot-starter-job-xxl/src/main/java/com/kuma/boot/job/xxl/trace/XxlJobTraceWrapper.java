package com.kuma.boot.job.xxl.trace;

import com.kuma.boot.common.utils.log.LogUtils;
import com.xxl.job.core.handler.IJobHandler;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.SpanNamer;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.internal.DefaultSpanNamer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class XxlJobTraceWrapper extends IJobHandler {
   private final BeanFactory beanFactory;
   private final IJobHandler delegate;
   private Tracer tracer;
   private SpanNamer spanNamer;

   public XxlJobTraceWrapper(BeanFactory beanFactory, IJobHandler delegate) {
      this.beanFactory = beanFactory;
      this.delegate = delegate;
   }

   public void execute() throws Exception {
      if (this.tracer == null) {
         try {
            this.tracer = (Tracer)this.beanFactory.getBean(Tracer.class);
         } catch (NoSuchBeanDefinitionException var2) {
            this.delegate.execute();
            return;
         }
      }

      this.doExecute();
   }

   private void doExecute() throws Exception {
      Span span = this.tracer.nextSpan().name("xxl-job").start();

      try {
         this.delegate.execute();
      } catch (Exception e) {
         span.error(e);
         throw e;
      } finally {
         span.end();
      }

   }

   private SpanNamer spanNamer() {
      if (this.spanNamer == null) {
         try {
            this.spanNamer = (SpanNamer)this.beanFactory.getBean(SpanNamer.class);
         } catch (NoSuchBeanDefinitionException var2) {
            LogUtils.warn("SpanNamer bean not found - will provide a manually created instance", new Object[0]);
            return new DefaultSpanNamer();
         }
      }

      return this.spanNamer;
   }
}
