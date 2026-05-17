package com.kuma.boot.xss.xsssupport;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.xss.autoconfigure.properties.XssProperties;
import com.kuma.boot.xss.utils.XssUtil;
import java.io.IOException;

public class XssCleanDeserializer extends XssCleanDeserializerBase {
   public XssCleanDeserializer() {
   }

   public String clean(String name, String text) throws IOException {
      if (text == null) {
         return null;
      } else {
         XssProperties properties = (XssProperties)ContextUtils.getBean(XssProperties.class);
         if (properties == null) {
            return text;
         } else {
            XssCleaner xssCleaner = (XssCleaner)ContextUtils.getBean(XssCleaner.class);
            if (xssCleaner == null) {
               return XssUtil.trim(text, properties.getTrimText());
            } else {
               String value = xssCleaner.clean(name, XssUtil.trim(text, properties.getTrimText()), XssType.JACKSON);
               LogUtils.debug("Json property name:{} value:{} cleaned up by xss, current value is:{}.", new Object[]{name, text, value});
               return value;
            }
         }
      }
   }
}
