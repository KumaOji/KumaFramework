package com.kuma.boot.sensitive.sensitivemvc.serializer;

import com.alibaba.fastjson2.filter.BeanContext;
import com.alibaba.fastjson2.filter.ContextValueFilter;

public class FastJsonSensitiveValueFilter extends AbstractFastJsonSensitiveValueFilter implements ContextValueFilter {
   public FastJsonSensitiveValueFilter() {
   }

   public Object process(BeanContext context, Object object, String name, Object value) {
      return this.process(object, name, value);
   }
}
