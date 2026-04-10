package com.kuma.boot.test.junitperf.util;

import cn.hutool.extra.template.TemplateException;
import com.kuma.boot.test.junitperf.support.exception.JunitPerfException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL
)
public final class FreemarkerUtil {
   private static Configuration configuration = null;

   private FreemarkerUtil() {
   }

   public static Configuration getConfiguration(String encoding) {
      return getConfiguration(encoding, true);
   }

   public static Configuration getConfiguration(String encoding, boolean isForce) {
      if (configuration == null || isForce) {
         configuration = new Configuration(Configuration.VERSION_2_3_30);
         configuration.setEncoding(Locale.getDefault(), encoding);
         configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_30));
         configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
         configuration.setObjectWrapper(new BeansWrapper(Configuration.VERSION_2_3_30));
      }

      return configuration;
   }

   public static boolean createFile(Template template, String targetFilePath, Map<String, Object> map, boolean isOverwriteWhenExists) throws JunitPerfException, IOException {
      boolean result = true;
      File file = new File(targetFilePath);
      boolean makeDirs = file.getParentFile().mkdirs();
      if (!file.exists()) {
         result = file.createNewFile();
         flushFileContent(template, map, file);
      } else if (file.exists() && isOverwriteWhenExists) {
         flushFileContent(template, map, file);
      }

      return result;
   }

   private static void flushFileContent(Template template, Map<String, Object> map, File file) throws JunitPerfException {
      try {
         Writer out = new BufferedWriter(new FileWriter(file));
         template.process(map, out);
         out.flush();
      } catch (TemplateException | IOException e) {
         throw new JunitPerfException(e);
      } catch (freemarker.template.TemplateException e) {
         throw new RuntimeException(e);
      }
   }
}
