package com.kuma.boot.xss.xsssupport;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.xss.utils.XssUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class XssStringJsonDeserializer extends ValueDeserializer<String> {
   public XssStringJsonDeserializer() {
   }

   public String deserialize(JsonParser p, DeserializationContext dc) throws JacksonException {
      if (!p.hasToken(JsonToken.VALUE_STRING)) {
         return null;
      } else {
         String value = p.getValueAsString();
         if (StrUtil.isEmpty(value)) {
            return value;
         } else {
            List<String> list = new ArrayList();
            list.add("<script>");
            list.add("</script>");
            list.add("<iframe>");
            list.add("</iframe>");
            list.add("<noscript>");
            list.add("</noscript>");
            list.add("<frameset>");
            list.add("</frameset>");
            list.add("<frame>");
            list.add("</frame>");
            list.add("<noframes>");
            list.add("</noframes>");
            list.add("<embed>");
            list.add("</embed>");
            list.add("<object>");
            list.add("</object>");
            list.add("<meta>");
            list.add("</meta>");
            list.add("<link>");
            list.add("</link>");
            Stream var10000 = list.stream();
            Objects.requireNonNull(value);
            return var10000.anyMatch(value::contains) ? XssUtil.xssClean(value, (List)null) : value;
         }
      }
   }
}
