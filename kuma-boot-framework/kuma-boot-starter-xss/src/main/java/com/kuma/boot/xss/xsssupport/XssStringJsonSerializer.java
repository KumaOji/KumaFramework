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
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.xss.utils.XssUtil;
import cn.hutool.core.util.StrUtil;
import tools.jackson.databind.ValueSerializer;

/**
 * 基于xss的 json 序列化器 在本项目中，没有使用该类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-03 08:05:50
 */
public class XssStringJsonSerializer extends ValueSerializer<String> {

   @Override
   public void serialize(String value, JsonGenerator jsonGenerator, SerializationContext ctxt) throws JacksonException {
      if (StrUtil.isEmpty(value)) {
         return;
      }
      try {
         String encodedValue = XssUtil.xssClean(value, null);
         jsonGenerator.writeString(encodedValue);
      } catch (Exception e) {
         LogUtils.error("序列化失败:[{}]", value, e);
      }
   }

   @Override
   public Class<String> handledType() {
      return String.class;
   }

}
