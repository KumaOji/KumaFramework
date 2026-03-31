package com.kuma.boot.monitor.strategy;

import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.monitor.collect.task.IOCollectTask;
import com.kuma.boot.monitor.model.Report;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultWarnStrategy implements WarnStrategy {
   protected static int maxCacheSize = 3;
   protected static List<Report> cacheReports;
   protected Rule.RulesAnalyzer rulesAnalyzer;
   protected WarnTemplate warnTemplate;

   public Rule.RulesAnalyzer getRulesAnalyzer() {
      return this.rulesAnalyzer;
   }

   public WarnTemplate getWarnTemplate() {
      return this.warnTemplate;
   }

   public DefaultWarnStrategy(WarnTemplate warnTemplate, Rule.RulesAnalyzer rulesAnalyzer) {
      this.warnTemplate = warnTemplate;
      this.rulesAnalyzer = rulesAnalyzer;
      this.setDefaultStrategy();
   }

   public void setDefaultStrategy() {
      this.rulesAnalyzer.registerRules("cpu.process", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.cpu.process", "[>0.7]"));
      this.rulesAnalyzer.registerRules("cpu.system", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.cpu.system", "[>0.7]"));
      this.rulesAnalyzer.registerRules("io.current.dir.usable.size", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.io.current.dir.usable.size", "[<500]"));
      this.rulesAnalyzer.registerRules("memery.jvm.max", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.memery.jvm.max", "[<256]"));
      this.rulesAnalyzer.registerRules("memery.system.free", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.memery.system.free", "[<256]"));
      this.rulesAnalyzer.registerRules("thread.deadlocked.count", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.thread.deadlocked.count", "[>10]"));
      this.rulesAnalyzer.registerRules("thread.total", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.thread.total", "[>1000]"));
      this.rulesAnalyzer.registerRules("tomcat.threadPool.pool.size.count", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.tomcat.threadPool.pool.size.count", "[>1000]"));
      this.rulesAnalyzer.registerRules("tomcat.threadPool.active.count", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.tomcat.threadPool.active.count", "[>200]"));
      this.rulesAnalyzer.registerRules("tomcat.threadPool.queue.size", (String)PropertyUtils.getPropertyCache("kmc.monitor.strategy.tomcat.threadPool.queue.size", "[>50]"));
      if (this.rulesAnalyzer.getRules("io.current.dir.usable.size") != null) {
         this.rulesAnalyzer.getRules("io.current.dir.usable.size").forEach((c) -> {
            if (c.getType() == Rule.RuleType.less) {
               c.setHitCallBack((value) -> IOCollectTask.clearLog());
            }

         });
      }

   }

   public Report analyse(Report report) {
      while(cacheReports.size() > maxCacheSize) {
         cacheReports.remove(0);
      }

      cacheReports.add(report);
      Report avgReport = report.avgReport(cacheReports);
      return this.rulesAnalyzer.analyse(avgReport);
   }

   public String analyseText(Report report) {
      Report r = this.analyse(report);
      StringBuilder warn = new StringBuilder();
      r.eachReport((filed, item) -> {
         if (item.isWarn()) {
            warn.append(this.warnTemplate.getWarnContent(filed, item.getDesc(), item.getValue(), item.getRule()));
         }

         return item;
      });
      String warnInfo = warn.toString();
      return StringUtils.isEmpty(warnInfo) ? "" : warnInfo;
   }

   static {
      cacheReports = Collections.synchronizedList(new ArrayList(maxCacheSize + 2));
   }
}
