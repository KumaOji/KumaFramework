package com.kuma.boot.xss.xsssupport;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.xss.autoconfigure.properties.XssProperties;
import com.kuma.boot.xss.utils.XssUtil;
import java.io.IOException;

public class JacksonXssClean extends XssCleanDeserializerBase {
   private final XssProperties properties;
   private final XssCleaner xssCleaner;

   public JacksonXssClean(XssProperties properties, XssCleaner xssCleaner) {
      this.properties = properties;
      this.xssCleaner = xssCleaner;
   }

   public String clean(String name, String text) throws IOException {
      if (text == null) {
         return null;
      } else if (XssHolder.isIgnore(name)) {
         return XssUtil.trim(text, this.properties.getTrimText());
      } else {
         String value = this.xssCleaner.clean(name, XssUtil.trim(text, this.properties.getTrimText()), XssType.JACKSON);
         LogUtils.debug("Json property name:{} value:{} cleaned up by xss, current value is:{}.", new Object[]{name, text, value});
         return value;
      }
   }
}
