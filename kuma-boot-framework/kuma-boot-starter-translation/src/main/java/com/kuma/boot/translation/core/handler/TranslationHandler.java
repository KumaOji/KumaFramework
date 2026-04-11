package com.kuma.boot.translation.core.handler;

import cn.hutool.core.util.ObjUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.reflect.ReflectUtils;
import com.kuma.boot.translation.annotation.Translation;
import com.kuma.boot.translation.core.TranslationInterface;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class TranslationHandler extends ValueSerializer<Object> {
   public static final Map<String, TranslationInterface<?>> TRANSLATION_MAPPER = new ConcurrentHashMap();
   private Translation translation;

   public TranslationHandler() {
   }

   public void serialize(Object value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
      TranslationInterface<?> trans = (TranslationInterface)TRANSLATION_MAPPER.get(this.translation.type());
      if (ObjUtil.isNotNull(trans)) {
         if (StringUtils.isNotBlank(this.translation.mapper())) {
            value = ReflectUtils.invokeGetter(gen.currentValue(), this.translation.mapper());
         }

         if (ObjUtil.isNull(value)) {
            gen.writeNull();
            return;
         }

         Object result = trans.translation(value, this.translation.other());
         gen.writeStartObject(result);
         gen.writeEndObject();
      } else {
         gen.writeStartObject(value);
         gen.writeEndObject();
      }

   }

   public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
      Translation translation = (Translation)property.getAnnotation(Translation.class);
      if (Objects.nonNull(translation)) {
         this.translation = translation;
         return this;
      } else {
         return ctxt.findValueSerializer(property.getType());
      }
   }
}
