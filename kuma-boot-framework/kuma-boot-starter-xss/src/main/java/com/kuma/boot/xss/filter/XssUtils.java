package com.kuma.boot.xss.filter;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.lang3.ObjectUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.util.ResourceUtils;

public class XssUtils {
   private static volatile XssUtils INSTANCE;
   private final AntiSamy antiSamy;
   private final String nbsp;
   private final String quot;

   private XssUtils() {
      Policy policy = this.createPolicy();
      this.antiSamy = ObjectUtils.isNotEmpty(policy) ? new AntiSamy(policy) : new AntiSamy();
      this.nbsp = this.cleanHtml("&nbsp;");
      this.quot = this.cleanHtml("\"");
   }

   private static XssUtils getInstance() {
      if (ObjectUtils.isEmpty(INSTANCE)) {
         synchronized(XssUtils.class) {
            if (ObjectUtils.isEmpty(INSTANCE)) {
               INSTANCE = new XssUtils();
            }
         }
      }

      return INSTANCE;
   }

   private Policy createPolicy() {
      try {
         URL url = ResourceUtils.getURL("classpath:antisamy/antisamy-anythinggoes.xml");
         return Policy.getInstance(url);
      } catch (PolicyException | IOException e) {
         LogUtils.trace("Antisamy create policy error! {}", new Object[]{((Exception)e).getMessage()});
         return null;
      }
   }

   private CleanResults scan(String taintedHtml) throws ScanException, PolicyException {
      return this.antiSamy.scan(taintedHtml);
   }

   private String cleanHtml(String taintedHtml) {
      try {
         LogUtils.trace("Before Antisamy Scan, value is: [{}]", new Object[]{taintedHtml});
         CleanResults cleanResults = this.scan(taintedHtml);
         String result = cleanResults.getCleanHTML();
         LogUtils.trace("After  Antisamy Scan, value is: [{}]", new Object[]{result});
         return result;
      } catch (PolicyException | ScanException e) {
         LogUtils.error("Antisamy scan catch error! {}", new Object[]{((Exception)e).getMessage()});
         return taintedHtml;
      }
   }

   public static String cleaning(String taintedHTML) {
      String cleanHtml = StringUtils.escapeHtml(getInstance().cleanHtml(taintedHTML));
      String temp = cleanHtml.replaceAll(getInstance().nbsp, "");
      String result = temp.replaceAll(getInstance().quot, "\"");
      LogUtils.trace("After  Antisamy Well Formed, value is: [{}]", new Object[]{result});
      return result;
   }
}
