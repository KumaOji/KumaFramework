package com.kuma.boot.monitor.monitor.monitor.db.mybatis;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.monitor.monitor.monitor.utils.Metrics;
import com.kuma.boot.monitor.monitor.monitor.utils.MetricsMethodInterceptor;
import com.kuma.boot.monitor.monitor.monitor.utils.Proxy;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

public class MetricMapperFactoryBean<T> implements FactoryBean<T> {
   private MapperFactoryBean<T> mapperFactoryBean;
   private ApplicationContext applicationContext;

   public MetricMapperFactoryBean(MapperFactoryBean<T> mapperFactoryBean, ApplicationContext applicationContext) {
      this.mapperFactoryBean = mapperFactoryBean;
      this.applicationContext = applicationContext;
   }

   public T getObject() throws Exception {
      Object target = this.mapperFactoryBean.getObject();
      String var10000 = Metrics.getApplicationName(this.applicationContext);
      String key = "mybatis_mapper_" + var10000 + "_" + this.mapperFactoryBean.getObjectType().getSimpleName();
      LogUtils.info("\u589e\u52a0mybatis\u9ed8\u8ba4\u76d1\u63a7:{}", new Object[]{key});
      return (T)Proxy.getProxy(target, new MetricsMethodInterceptor(key));
   }

   public Class<?> getObjectType() {
      return this.mapperFactoryBean.getObjectType();
   }

   public boolean isSingleton() {
      return this.mapperFactoryBean.isSingleton();
   }
}
