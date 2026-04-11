package com.kuma.boot.xss.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.springframework.util.StringUtils;

public class XssUtils {
   public static final XssUtil.HtmlSafeList WHITE_LIST;

   public XssUtils() {
   }

   public static String trim(String text, boolean trim) {
      return trim ? text.trim() : text;
   }

   public static String clean(String html) {
      return StringUtils.hasText(html) ? Jsoup.clean(html, WHITE_LIST) : html;
   }

   static {
      WHITE_LIST = XssUtil.HtmlSafeList.INSTANCE;
   }

   public static class HtmlSafeList extends Safelist {
      public static final XssUtil.HtmlSafeList INSTANCE = new XssUtil.HtmlSafeList();

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
