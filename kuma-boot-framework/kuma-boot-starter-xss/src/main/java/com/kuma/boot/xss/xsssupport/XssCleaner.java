package com.kuma.boot.xss.xsssupport;

import com.kuma.boot.xss.utils.XssUtil;
import org.jsoup.Jsoup;

public interface XssCleaner {
   default String clean(String value, XssType type) {
      return this.clean((String)null, value, type);
   }

   String clean(String name, String value, XssType type);

   default boolean isValid(String html) {
      return Jsoup.isValid(html, XssUtil.WHITE_LIST);
   }
}
