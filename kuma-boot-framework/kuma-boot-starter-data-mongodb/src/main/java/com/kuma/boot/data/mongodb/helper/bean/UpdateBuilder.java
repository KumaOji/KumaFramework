package com.kuma.boot.data.mongodb.helper.bean;

import com.kuma.boot.data.mongodb.helper.reflection.ReflectionUtil;
import com.kuma.boot.data.mongodb.helper.reflection.SerializableFunction;
import org.springframework.data.mongodb.core.query.Update;

public class UpdateBuilder {
   Update update = new Update();

   public UpdateBuilder() {
   }

   public <E, R> UpdateBuilder(SerializableFunction<E, R> key, Object value) {
      this.update.set(ReflectionUtil.getFieldName(key), value);
   }

   public <E, R> UpdateBuilder set(SerializableFunction<E, R> key, Object value) {
      this.update.set(ReflectionUtil.getFieldName(key), value);
      return this;
   }

   public <E, R> UpdateBuilder inc(SerializableFunction<E, R> key, Number count) {
      this.update.inc(ReflectionUtil.getFieldName(key), count);
      return this;
   }

   public Update toUpdate() {
      return this.update;
   }
}
