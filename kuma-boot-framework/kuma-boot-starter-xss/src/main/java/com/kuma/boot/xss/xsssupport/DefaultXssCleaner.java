package com.kuma.boot.xss.xsssupport;

import com.kuma.boot.xss.autoconfigure.properties.XssProperties;
import com.kuma.boot.xss.utils.XssUtil;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Entities.EscapeMode;
import org.springframework.web.util.HtmlUtils;

public class DefaultXssCleaner implements XssCleaner {
   private final XssProperties properties;

   public DefaultXssCleaner(XssProperties properties) {
      this.properties = properties;
   }

   private static Document.OutputSettings getOutputSettings(XssProperties properties) {
      return (new Document.OutputSettings()).escapeMode(EscapeMode.xhtml).prettyPrint(properties.getPrettyPrint());
   }

   public String clean(String name, String bodyHtml, XssType type) {
      if (StringUtil.isBlank(bodyHtml)) {
         return bodyHtml;
      } else {
         XssProperties.Mode mode = this.properties.getMode();
         if (XssProperties.Mode.escape == mode) {
            return HtmlUtils.htmlEscape(bodyHtml, StandardCharsets.UTF_8.name());
         } else if (XssProperties.Mode.validate == mode) {
            if (Jsoup.isValid(bodyHtml, XssUtil.WHITE_LIST)) {
               return bodyHtml;
            } else {
               throw type.getXssException(name, bodyHtml, "Xss validate fail, input value:" + bodyHtml);
            }
         } else {
            String escapedHtml = Jsoup.clean(bodyHtml, "", XssUtil.WHITE_LIST, getOutputSettings(this.properties));
            return this.properties.getEnableEscape() ? escapedHtml : Entities.unescape(escapedHtml);
         }
      }
   }
}
