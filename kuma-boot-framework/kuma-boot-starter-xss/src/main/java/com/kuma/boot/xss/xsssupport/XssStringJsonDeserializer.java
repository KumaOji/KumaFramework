/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.xss.xsssupport;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import com.kuma.boot.xss.utils.XssUtil;
import cn.hutool.core.util.StrUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.databind.ValueDeserializer;

/**
 * 过滤跨站脚本的 反序列化工具
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-03 08:05:48
 */
public class XssStringJsonDeserializer extends ValueDeserializer<String> {

   @Override
   public String deserialize(JsonParser p, DeserializationContext dc) throws JacksonException {
      if (!p.hasToken(JsonToken.VALUE_STRING)) {
         return null;
      }
      String value = p.getValueAsString();
      if (StrUtil.isEmpty(value)) {
         return value;
      }

      List<String> list = new ArrayList<>();
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
      // list.add("<style>");
      // list.add("</style>");
      list.add("<meta>");
      list.add("</meta>");
      list.add("<link>");
      list.add("</link>");
      if (list.stream().anyMatch(value::contains)) {
         return XssUtil.xssClean(value, null);
      }
      return value;
   }
}
