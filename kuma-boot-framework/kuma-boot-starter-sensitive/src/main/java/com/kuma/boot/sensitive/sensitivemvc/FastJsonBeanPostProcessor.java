package com.kuma.boot.sensitive.sensitivemvc;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sensitive.sensitivemvc.serializer.FastJsonSensitiveValueFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class FastJsonBeanPostProcessor implements BeanPostProcessor {
   public FastJsonBeanPostProcessor() {
   }

   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      if (bean instanceof FastJsonHttpMessageConverter fastJsonConverter) {
         FastJsonConfig fastJsonConfig = fastJsonConverter.getFastJsonConfig();
         Filter[] oldReadFilters = fastJsonConfig.getReaderFilters();
         Filter[] oldWriterFilters = fastJsonConfig.getWriterFilters();
         Filter[] injectFilters = new Filter[]{new FastJsonSensitiveValueFilter()};
         Filter[] newReadFilters = (Filter[])ArrayUtil.addAll(new Filter[][]{oldReadFilters, injectFilters});
         Filter[] newWriterFilters = (Filter[])ArrayUtil.addAll(new Filter[][]{oldWriterFilters, injectFilters});
         fastJsonConfig.setReaderFilters(newReadFilters);
         fastJsonConfig.setWriterFilters(newWriterFilters);
         LogUtils.info("Injected [{}] WriterFilter to [{}]", new Object[]{FastJsonSensitiveValueFilter.class.getName(), FastJsonHttpMessageConverter.class.getName()});
         return fastJsonConverter;
      } else {
         return bean;
      }
   }
}
