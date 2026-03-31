package com.kuma.boot.monitor.collect;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.Monitor;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.autoconfigure.properties.MonitorProperties;
import com.kuma.boot.monitor.collect.task.AsyncThreadPoolCollectTask;
import com.kuma.boot.monitor.collect.task.CpuCollectTask;
import com.kuma.boot.monitor.collect.task.DataSourceCollectTask;
import com.kuma.boot.monitor.collect.task.DoubtApiCollectTask;
import com.kuma.boot.monitor.collect.task.ElkCollectTask;
import com.kuma.boot.monitor.collect.task.HttpPoolCollectTask;
import com.kuma.boot.monitor.collect.task.LogStatisticCollectTask;
import com.kuma.boot.monitor.collect.task.MemoryCollectTask;
import com.kuma.boot.monitor.collect.task.MonitorThreadPoolCollectTask;
import com.kuma.boot.monitor.collect.task.MybatisCollectTask;
import com.kuma.boot.monitor.collect.task.NacosCollectTask;
import com.kuma.boot.monitor.collect.task.NetworkCollectTask;
import com.kuma.boot.monitor.collect.task.ThreadCollectTask;
import com.kuma.boot.monitor.collect.task.UnCatchExceptionCollectTask;
import com.kuma.boot.monitor.collect.task.WebServerCollectTask;
import com.kuma.boot.monitor.collect.task.XxlJobCollectTask;
import com.kuma.boot.monitor.enums.WarnTypeEnum;
import com.kuma.boot.monitor.model.Report;
import com.kuma.boot.monitor.strategy.DefaultWarnStrategy;
import com.kuma.boot.monitor.strategy.WarnStrategy;
import java.util.ArrayList;
import java.util.List;

public class HealthCheckProvider implements AutoCloseable {
   private List<AbstractCollectTask> checkTasks = new ArrayList();
   private boolean close;
   private Monitor monitor;
   private WarnStrategy strategy;
   private MonitorProperties monitorProperties;
   private CollectTaskProperties collectTaskProperties;

   public void registerCollectTask(AbstractCollectTask task) {
      this.checkTasks.add(task);
   }

   public HealthCheckProvider(CollectTaskProperties collectTaskProperties, MonitorProperties monitorProperties, WarnStrategy strategy, Monitor monitor) {
      this.strategy = strategy;
      this.close = false;
      this.collectTaskProperties = collectTaskProperties;
      this.monitorProperties = monitorProperties;
      this.monitor = monitor;
      this.registerCollectTask(new CpuCollectTask(collectTaskProperties));
      this.registerCollectTask(new MemoryCollectTask(collectTaskProperties));
      this.registerCollectTask(new ThreadCollectTask(collectTaskProperties));
      this.registerCollectTask(new UnCatchExceptionCollectTask(collectTaskProperties));
      this.registerCollectTask(new MonitorThreadPoolCollectTask(collectTaskProperties, monitor.getMonitorThreadPoolExecutor()));
      this.registerCollectTask(new AsyncThreadPoolCollectTask(collectTaskProperties, monitor.getAsyncThreadPoolExecutor()));
      this.registerCollectTask(new MybatisCollectTask(collectTaskProperties));
      this.registerCollectTask(new DataSourceCollectTask(collectTaskProperties));
      this.registerCollectTask(new WebServerCollectTask(collectTaskProperties));
      this.registerCollectTask(new NetworkCollectTask(collectTaskProperties));
      this.registerCollectTask(new XxlJobCollectTask(collectTaskProperties));
      this.registerCollectTask(new HttpPoolCollectTask(collectTaskProperties));
      this.registerCollectTask(new ElkCollectTask(collectTaskProperties));
      this.registerCollectTask(new DoubtApiCollectTask(collectTaskProperties));
      this.registerCollectTask(new LogStatisticCollectTask(collectTaskProperties));
      this.registerCollectTask(new NacosCollectTask(collectTaskProperties));
      monitor.monitorSubmit("\u7cfb\u7edf\u4efb\u52a1: MonitorCheckProvider \u91c7\u96c6\u4efb\u52a1", (Runnable)(() -> {
         while(!monitor.monitorIsShutdown() && !this.close) {
            try {
               Report report = this.getReport(false);
               String text = strategy.analyseText(report);
               if (StringUtils.isEmpty(text)) {
                  return;
               }

               AbstractCollectTask.notifyMessage(WarnTypeEnum.ERROR, "\u5065\u5eb7\u68c0\u67e5", text);
            } catch (Exception e) {
               LogUtils.warn("kuma-boot-starter-monitor", new Object[]{"run \u5faa\u73af\u91c7\u96c6\u51fa\u9519", e});
            }

            try {
               Thread.sleep((long)monitorProperties.getTimeSpan() * 1000L);
            } catch (Exception e) {
               LogUtils.error(e);
            }
         }

      }));
   }

   public Report getReport(boolean isAnalyse) {
      Report report = (new Report()).setDesc("\u5065\u5eb7\u68c0\u67e5\u62a5\u8868").setName("kmc.monitor.report");

      for(AbstractCollectTask task : this.checkTasks) {
         if (task.getEnabled()) {
            try {
               Report report2 = task.getReport();
               if (report2 != null) {
                  report.put(task.getName(), report2.setDesc(task.getDesc()).setName(task.getName()));
               }
            } catch (Exception e) {
               LogUtils.error(e, "kuma-boot-starter-monitor" + task.getName() + "\u91c7\u96c6\u83b7\u53d6\u62a5\u8868\u51fa\u9519", new Object[0]);
            }
         }
      }

      if (isAnalyse) {
         report = this.strategy.analyse(report);
      }

      return report;
   }

   public void close() {
      this.close = true;
      this.monitor.monitorShutdown();

      for(AbstractCollectTask task : this.checkTasks) {
         try {
            task.close();
         } catch (Exception exp) {
            LogUtils.warn("kuma-boot-starter-monitor", new Object[]{"close\u8d44\u6e90\u91ca\u653e\u51fa\u9519", exp});
         }
      }

   }

   public List<AbstractCollectTask> getCheckTasks() {
      return this.checkTasks;
   }

   public void setCheckTasks(List<AbstractCollectTask> checkTasks) {
      this.checkTasks = checkTasks;
   }

   public WarnStrategy getStrategy() {
      return this.strategy;
   }

   public void setStrategy(DefaultWarnStrategy strategy) {
      this.strategy = strategy;
   }

   public boolean isClose() {
      return this.close;
   }

   public void setClose(boolean close) {
      this.close = close;
   }

   public MonitorProperties getHealthProperties() {
      return this.monitorProperties;
   }

   public void setHealthProperties(MonitorProperties monitorProperties) {
      this.monitorProperties = monitorProperties;
   }

   public Monitor getMonitor() {
      return this.monitor;
   }

   public void setMonitor(Monitor monitor) {
      this.monitor = monitor;
   }

   public void setStrategy(WarnStrategy strategy) {
      this.strategy = strategy;
   }

   public CollectTaskProperties getCollectTaskProperties() {
      return this.collectTaskProperties;
   }

   public void setCollectTaskProperties(CollectTaskProperties collectTaskProperties) {
      this.collectTaskProperties = collectTaskProperties;
   }
}
