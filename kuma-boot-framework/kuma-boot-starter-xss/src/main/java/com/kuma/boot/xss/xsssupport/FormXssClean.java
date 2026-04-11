package com.kuma.boot.xss.xsssupport;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.xss.autoconfigure.properties.XssProperties;
import com.kuma.boot.xss.utils.XssUtil;
import java.beans.PropertyEditorSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
@ConditionalOnProperty(
   prefix = "kuma.boot.xss",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class FormXssClean {
   private final XssProperties properties;
   private final XssCleaner xssCleaner;

   public FormXssClean(XssProperties properties, XssCleaner xssCleaner) {
      this.properties = properties;
      this.xssCleaner = xssCleaner;
   }

   @InitBinder
   public void initBinder(WebDataBinder binder) {
      binder.registerCustomEditor(String.class, new StringPropertiesEditor(this.xssCleaner, this.properties));
   }

   public static class StringPropertiesEditor extends PropertyEditorSupport {
      private final XssCleaner xssCleaner;
      private final XssProperties properties;

      public StringPropertiesEditor(XssCleaner xssCleaner, XssProperties properties) {
         this.xssCleaner = xssCleaner;
         this.properties = properties;
      }

      public String getAsText() {
         Object value = this.getValue();
         return value != null ? value.toString() : "";
      }

      public void setAsText(String text) throws IllegalArgumentException {
         if (text == null) {
            this.setValue((Object)null);
         } else if (XssHolder.isEnabled()) {
            String value = this.xssCleaner.clean(XssUtil.trim(text, this.properties.getTrimText()), XssType.FORM);
            this.setValue(value);
            LogUtils.debug("Request parameter value:{} cleaned up by xss, current value is:{}.", new Object[]{text, value});
         } else {
            this.setValue(XssUtil.trim(text, this.properties.getTrimText()));
         }

      }
   }
}
