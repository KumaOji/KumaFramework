package com.kuma.boot.monitor.monitor.monitor.dubbo;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.threadpool.manager.ExecutorRepository;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;

public class DubboMonitor {
   private MeterRegistry meterRegistry;

   public DubboMonitor(MeterRegistry meterRegistry) {
      this.meterRegistry = meterRegistry;
   }

   public void init(MeterRegistry registry) {
      ExecutorRepository executorRepository = (ExecutorRepository)ExtensionLoader.getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
      Collection<Exporter<?>> exporters = DubboProtocol.getDubboProtocol().getExporters();
      exporters.forEach((exporter) -> {
         ExecutorService executorService = executorRepository.getExecutor(exporter.getInvoker().getUrl());
         String executorServiceName = this.executorServiceName(exporter.getInvoker().getUrl());
         ExecutorServiceMetrics executorServiceMetrics = new ExecutorServiceMetrics(executorService, executorServiceName, this.tags());
         executorServiceMetrics.bindTo(registry);
      });
   }

   protected List<Tag> tags() {
      List<Tag> tags = new ArrayList(1);
      return tags;
   }

   protected String executorServiceName(URL url) {
      return "dubboThreadPoolName";
   }
}
