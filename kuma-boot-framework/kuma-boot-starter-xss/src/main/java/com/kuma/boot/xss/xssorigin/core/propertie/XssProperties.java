package com.kuma.boot.xss.xssorigin.core.propertie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.xss.origin"
)
public class XssProperties {
   private boolean enable = true;
   private List<String> ignoreUrls = new ArrayList();
   private List<String> ignorePaths = List.of("favicon.ico", "/**/doc.html", "/**/swagger-ui.html", "/csrf", "/webjars/**", "/v3/**", "/swagger-resources/**", "/resources/**", "/static/**", "/public/**", "/classpath:*", "/actuator/**", "/**/noxss/**", "/**/activiti/**", "/**/service/model/**", "/**/service/editor/**");

   public XssProperties() {
   }

   public boolean isEnable() {
      return this.enable;
   }

   public void setEnable(boolean enable) {
      this.enable = enable;
   }

   public List<String> getIgnoreUrls() {
      List<String> tempList = new ArrayList(this.ignoreUrls);
      tempList.addAll(new ArrayList(this.ignorePaths));
      return Collections.unmodifiableList(tempList);
   }

   public void setIgnoreUrls(List<String> ignoreUrls) {
      this.ignoreUrls = ignoreUrls;
   }
}
