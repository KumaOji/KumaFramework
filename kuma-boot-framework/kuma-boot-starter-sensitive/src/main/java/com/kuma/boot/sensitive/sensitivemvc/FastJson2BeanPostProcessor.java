package com.kuma.boot.sensitive.sensitivemvc;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sensitive.sensitivemvc.serializer.FastJson2SensitiveValueFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class FastJson2BeanPostProcessor implements BeanPostProcessor {
   public FastJson2BeanPostProcessor() {
   }

   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      if (bean instanceof FastJsonHttpMessageConverter fastJsonConverter) {
         FastJsonConfig fastJsonConfig = fastJsonConverter.getFastJsonConfig();
         Filter[] oldWriterFilters = fastJsonConfig.getWriterFilters();
         FastJson2SensitiveValueFilter[] injectWriterFilters = new FastJson2SensitiveValueFilter[]{new FastJson2SensitiveValueFilter()};
         Filter[] newWriterFilters = (Filter[])ArrayUtil.addAll(new Filter[][]{oldWriterFilters, injectWriterFilters});
         fastJsonConfig.setWriterFilters(newWriterFilters);
         LogUtils.info("Injected [{}] WriterFilter to [{}]", new Object[]{FastJson2SensitiveValueFilter.class.getName(), FastJsonHttpMessageConverter.class.getName()});
         return fastJsonConverter;
      } else {
         return bean;
      }
   }
}
