package com.kuma.boot.xss.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.util.StringUtils;

public class XssUtil {
   private static final String ANTISAMY_SLASHDOT_XML = "antisamy-slashdot-1.4.4.xml";
   private static Policy policy = null;
   private static final Pattern SCRIPT_BETWEEN_PATTERN = Pattern.compile("<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | ]*>", 2);
   private static final Pattern SCRIPT_END_PATTERN = Pattern.compile("</[\r\n| | ]*script[\r\n| | ]*>", 2);
   private static final Pattern SCRIPT_START_PATTERN = Pattern.compile("<[\r\n| | ]*script(.*?)>", 42);
   private static final Pattern EVAL_PATTERN = Pattern.compile("eval\\((.*?)\\)", 42);
   private static final Pattern E_XPRESSION_PATTERN = Pattern.compile("e-xpression\\((.*?)\\)", 42);
   private static final Pattern MOCHA_PATTERN = Pattern.compile("mocha[\r\n| | ]*:[\r\n| | ]*", 42);
   private static final Pattern EXPRESSION_PATTERN = Pattern.compile("expression\\((.*?)\\)", 42);
   private static final Pattern URL_PATTERN = Pattern.compile("url\\((.*?)\\)", 42);
   private static final Pattern VBSCRIPT_PATTERN = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", 42);
   private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("javascript[\r\n| | ]*:[\r\n| | ]*", 2);
   private static final Pattern ONLOAD_PATTERN = Pattern.compile("onload(.*?)=", 42);
   private static final Pattern ONMOUSEOVER_PATTERN = Pattern.compile("onMouseOver=.*?//", 42);
   private static final Pattern ONMOUSEOVER_PATTERN_2 = Pattern.compile("onmouseover(.*)", 42);
   private static final Pattern ONMOUSEOVER_PATTERN_3 = Pattern.compile("onmouseover=.*?", 42);
   private static final Pattern ALERT_PATTERN = Pattern.compile("alert(.*)", 42);
   private static String REPLACE_STRING = "";
   private static Pattern script = null;
   public static final HtmlSafeList WHITE_LIST;

   public XssUtil() {
   }

   public static String xssClean(String paramValue, List<String> ignoreParamValueList) {
      AntiSamy antiSamy = new AntiSamy();

      try {
         LogUtils.debug("raw value before xssClean: " + paramValue, new Object[0]);
         if (isIgnoreParamValue(paramValue, ignoreParamValueList)) {
            LogUtils.debug("ignore the xssClean,keep the raw paramValue: " + paramValue, new Object[0]);
            return paramValue;
         }

         CleanResults cr = antiSamy.scan(paramValue, policy);
         cr.getErrorMessages().forEach((x$0) -> LogUtils.debug(x$0, new Object[0]));
         String str = cr.getCleanHTML();
         str = stripXSSAndSql(str);
         str = str.replaceAll("&quot;", "\"");
         str = str.replaceAll("&amp;", "&");
         str = str.replaceAll("&lt;", "<");
         str = str.replaceAll("&gt;", ">");
         LogUtils.debug("xss filter value after xssClean" + str, new Object[0]);
         return str;
      } catch (ScanException e) {
         LogUtils.error("scan failed is [" + paramValue + "]", new Object[]{e});
      } catch (PolicyException e) {
         LogUtils.error("antisamy convert failed  is [" + paramValue + "]", new Object[]{e});
      }

      return paramValue;
   }

   public static String xssClean(String paramValue, List<String> ignoreParamValueList, String param) {
      return isIgnoreParamValue(param, ignoreParamValueList) ? stripXSSAndSql(paramValue) : xssClean(paramValue, ignoreParamValueList);
   }

   public static String stripXSSAndSql(String value) {
      if (StrUtil.isBlank(value)) {
         return value;
      } else {
         value = SCRIPT_BETWEEN_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = SCRIPT_END_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = SCRIPT_START_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = EVAL_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = E_XPRESSION_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = MOCHA_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = EXPRESSION_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = URL_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = VBSCRIPT_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = JAVASCRIPT_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = ONLOAD_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = ONMOUSEOVER_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         value = ONMOUSEOVER_PATTERN_2.matcher(value).replaceAll(REPLACE_STRING);
         value = ONMOUSEOVER_PATTERN_3.matcher(value).replaceAll(REPLACE_STRING);
         value = ALERT_PATTERN.matcher(value).replaceAll(REPLACE_STRING);
         return value;
      }
   }

   private static boolean isIgnoreParamValue(String paramValue, List<String> ignoreParamValueList) {
      if (StrUtil.isBlank(paramValue)) {
         return true;
      } else if (CollUtil.isEmpty(ignoreParamValueList)) {
         return false;
      } else {
         for(String ignoreParamValue : ignoreParamValueList) {
            if (paramValue.contains(ignoreParamValue)) {
               return true;
            }
         }

         return false;
      }
   }

   public static String trim(String text, boolean trim) {
      return trim ? text.strip() : text;
   }

   public static String clean(String html) {
      return StringUtils.hasText(html) ? Jsoup.clean(html, WHITE_LIST) : html;
   }

   static {
      script = Pattern.compile("<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | ]*>", 2);
      LogUtils.debug(" start read XSS config file [antisamy-slashdot-1.4.4.xml]", new Object[0]);
      InputStream inputStream = XssUtil.class.getClassLoader().getResourceAsStream("antisamy-slashdot-1.4.4.xml");

      try {
         policy = Policy.getInstance(inputStream);
         LogUtils.debug("read XSS config file [antisamy-slashdot-1.4.4.xml] success", new Object[0]);
      } catch (PolicyException e) {
         LogUtils.error("read XSS config file [antisamy-slashdot-1.4.4.xml] fail , reason:", new Object[]{e});
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException e) {
               LogUtils.error("close XSS config file [antisamy-slashdot-1.4.4.xml] fail , reason:", new Object[]{e});
            }
         }

      }

      WHITE_LIST = XssUtil.HtmlSafeList.INSTANCE;
   }

   public static class HtmlSafeList extends Safelist {
      public static final HtmlSafeList INSTANCE = new HtmlSafeList();

      public HtmlSafeList() {
         this.addTags(new String[]{"a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "span", "embed", "object", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul"});
         this.addAttributes("a", new String[]{"href", "title", "target"});
         this.addAttributes("blockquote", new String[]{"cite"});
         this.addAttributes("col", new String[]{"span"});
         this.addAttributes("colgroup", new String[]{"span"});
         this.addAttributes("img", new String[]{"align", "alt", "src", "title"});
         this.addAttributes("ol", new String[]{"start"});
         this.addAttributes("q", new String[]{"cite"});
         this.addAttributes("table", new String[]{"summary"});
         this.addAttributes("td", new String[]{"abbr", "axis", "colspan", "rowspan", "width"});
         this.addAttributes("th", new String[]{"abbr", "axis", "colspan", "rowspan", "scope", "width"});
         this.addAttributes("video", new String[]{"src", "autoplay", "controls", "loop", "muted", "poster", "preload"});
         this.addAttributes("object", new String[]{"width", "height", "classid", "codebase"});
         this.addAttributes("param", new String[]{"name", "value"});
         this.addAttributes("embed", new String[]{"src", "quality", "width", "height", "allowFullScreen", "allowScriptAccess", "flashvars", "name", "type", "pluginspage"});
         this.addAttributes(":all", new String[]{"class", "style", "height", "width", "type", "id", "name"});
         this.addProtocols("blockquote", "cite", new String[]{"http", "https"});
         this.addProtocols("cite", "cite", new String[]{"http", "https"});
         this.addProtocols("q", "cite", new String[]{"http", "https"});
      }

      public boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
         if ("src".equalsIgnoreCase(attr.getKey()) || "href".equalsIgnoreCase(attr.getKey())) {
            String value = attr.getValue();
            if (StringUtils.hasText(value) && value.toLowerCase().startsWith("javascript")) {
               return false;
            }
         }

         return "img".equals(tagName) && "src".equals(attr.getKey()) && attr.getValue().startsWith("data:;base64") ? true : super.isSafeAttribute(tagName, el, attr);
      }
   }
}
