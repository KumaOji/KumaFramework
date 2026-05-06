package com.kuma.boot.xss.autoconfigure.properties;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.xss"
)
public class XssProperties {
   public static final String PREFIX = "kuma.boot.xss";
   private Boolean enabled = false;
   private int order = 1;
   private boolean trimText = true;
   private Mode mode;
   private boolean prettyPrint;
   private boolean enableEscape;
   private List<String> pathPatterns;
   private List<String> pathExcludePatterns;
   private List<String> patterns;
   private List<String> ignorePaths;
   private List<String> ignoreParamValues;

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean getTrimText() {
      return this.trimText;
   }

   public void setTrimText(boolean trimText) {
      this.trimText = trimText;
   }

   public Mode getMode() {
      return this.mode;
   }

   public void setMode(Mode mode) {
      this.mode = mode;
   }

   public boolean getPrettyPrint() {
      return this.prettyPrint;
   }

   public void setPrettyPrint(boolean prettyPrint) {
      this.prettyPrint = prettyPrint;
   }

   public boolean getEnableEscape() {
      return this.enableEscape;
   }

   public void setEnableEscape(boolean enableEscape) {
      this.enableEscape = enableEscape;
   }

   public List<String> getPathPatterns() {
      return this.pathPatterns;
   }

   public void setPathPatterns(List<String> pathPatterns) {
      this.pathPatterns = pathPatterns;
   }

   public List<String> getPathExcludePatterns() {
      return this.pathExcludePatterns;
   }

   public void setPathExcludePatterns(List<String> pathExcludePatterns) {
      this.pathExcludePatterns = pathExcludePatterns;
   }

   public XssProperties() {
      this.mode = XssProperties.Mode.clear;
      this.prettyPrint = false;
      this.enableEscape = false;
      this.pathPatterns = new ArrayList();
      this.pathExcludePatterns = new ArrayList();
      this.patterns = List.of("/*");
      this.ignorePaths = List.of("favicon.ico", "/**/doc.html", "/**/swagger-ui.html", "/csrf", "/webjars/**", "/v3/**", "/swagger-resources/**", "/resources/**", "/static/**", "/public/**", "/classpath:*", "/actuator/**", "/**/noxss/**", "/**/activiti/**", "/**/service/model/**", "/**/service/editor/**");
      this.ignoreParamValues = List.of("noxss");
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public int getOrder() {
      return this.order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public List<String> getPatterns() {
      return this.patterns;
   }

   public void setPatterns(List<String> patterns) {
      this.patterns = patterns;
   }

   public List<String> getIgnorePaths() {
      return this.ignorePaths;
   }

   public void setIgnorePaths(List<String> ignorePaths) {
      this.ignorePaths = ignorePaths;
   }

   public List<String> getIgnoreParamValues() {
      return this.ignoreParamValues;
   }

   public void setIgnoreParamValues(List<String> ignoreParamValues) {
      this.ignoreParamValues = ignoreParamValues;
   }

   public static enum Mode {
      clear,
      escape,
      validate;

      private Mode() {
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{clear, escape, validate};
      }
   }
}
