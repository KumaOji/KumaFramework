package com.kuma.boot.test.junitperf.core.report.impl;

import com.kuma.boot.core.utils.io.PathUtils;
import com.kuma.boot.common.utils.lang.ConsoleUtils;
import com.kuma.boot.test.junitperf.core.report.Reporter;
import com.kuma.boot.test.junitperf.model.evaluation.EvaluationContext;
import com.kuma.boot.test.junitperf.support.i18n.I18N;
import com.kuma.boot.test.junitperf.util.FreemarkerUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class HtmlReporter implements Reporter {
   private static final String DEFAULT_REPORT_PACKAGE = System.getProperty("user.dir") + "/build/reports/kmctest/";
   private static final String REPORT_TEMPLATE = "/templates/";

   public HtmlReporter() {
   }

   public void report(Class testClass, Collection<EvaluationContext> evaluationContextSet) {
      Path outputPath = Paths.get(DEFAULT_REPORT_PACKAGE + PathUtils.packageToPath(testClass.getName()) + ".html");

      try {
         Configuration configuration = FreemarkerUtil.getConfiguration("UTF-8");
         configuration.setClassForTemplateLoading(FreemarkerUtil.class, "/templates/");
         Template template = configuration.getTemplate("report.ftl");
         Files.createDirectories(outputPath.getParent());
         ConsoleUtils.info("Rendering report to: " + String.valueOf(outputPath), new Object[0]);
         Map<String, Object> root = new HashMap<>();
         root.put("className", testClass.getSimpleName());
         root.put("contextData", evaluationContextSet);
         root.put("milliseconds", TimeUnit.MILLISECONDS);
         root.put("i18n", I18N.buildI18nVo());
         FreemarkerUtil.createFile(template, outputPath.toString(), root, true);
      } catch (Exception e) {
         ConsoleUtils.info("HtmlReporter meet ex: {}", new Object[]{e, e});
      }

   }
}
