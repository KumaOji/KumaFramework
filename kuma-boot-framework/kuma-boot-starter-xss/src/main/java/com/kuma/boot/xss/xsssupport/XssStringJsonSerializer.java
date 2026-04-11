package com.kuma.boot.xss.xsssupport;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.xss.utils.XssUtil;
import java.util.List;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class XssStringJsonSerializer extends ValueSerializer<String> {
   public XssStringJsonSerializer() {
   }

   public void serialize(String value, JsonGenerator jsonGenerator, SerializationContext ctxt) throws JacksonException {
      if (!StrUtil.isEmpty(value)) {
         try {
            String encodedValue = XssUtil.xssClean(value, (List)null);
            jsonGenerator.writeString(encodedValue);
         } catch (Exception e) {
            LogUtils.error("\u5e8f\u5217\u5316\u5931\u8d25:[{}]", new Object[]{value, e});
         }

      }
   }

   public Class<String> handledType() {
      return String.class;
   }
}
