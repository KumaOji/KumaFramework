package com.kuma.boot.flowengine;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.flowengine.engine.FlowEngine;
import com.kuma.boot.flowengine.exception.FlowEngineNestException;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.module.Flow;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public abstract class AbstractFlowContext extends PathMatchingResourcePatternResolver implements FlowContext, Lifecycle, ApplicationContextAware {
   private static final Logger logger = LoggerFactory.getLogger(AbstractFlowContext.class);
   private FlowDefXmlDocumentReader flowDefXmLDocumentReader = new FlowDefXmlDocumentReader(this);
   protected FlowEngine flowEngine;
   private ApplicationContext applicationContext;
   private Set<Class<? extends Throwable>> retryExceptions = new HashSet();

   public AbstractFlowContext() {
   }

   public void afterPropertiesSet() throws Exception {
      this.flowEngine = new FlowEngine();
      AutowireCapableBeanFactory autowireBeanFactory = this.applicationContext.getAutowireCapableBeanFactory();
      autowireBeanFactory.autowireBeanProperties(this.flowEngine, 0, false);
      autowireBeanFactory.initializeBean(this.flowEngine, FlowEngine.class.getName());
      RetryExceptionRegistry registry = new RetryExceptionRegistry(this.retryExceptions);
      this.applicationContext.getBeansOfType(GlobalRetryExceptionsCustomizer.class).values().forEach((r) -> r.customize(registry));
   }

   public synchronized void loadDefinition(String location) {
      if (logger.isInfoEnabled()) {
         logger.info("\u6d41\u7a0b\u5b9a\u4e49\u52a0\u8f7d\u914d\u7f6e\u8def\u5f84\uff1a{}", location);
      }

      try {
         Resource[] resources = this.getResources(location);
         int i = 0;

         for(int j = resources.length; i < j; ++i) {
            Resource resource = resources[i];
            if (logger.isInfoEnabled()) {
               logger.info("\u52a0\u8f7d\u914d\u7f6e\u6587\u4ef6:{}", resource.getURL());
            }

            if (!resource.exists()) {
               throw new FlowException(String.format("%s\u6d41\u7a0b\u5b9a\u4e49\u6587\u4ef6\u4e0d\u5b58\u5728", resource.getURL()));
            }

            this.flowDefXmLDocumentReader.analyze(resource);
         }

      } catch (IOException e) {
         throw new FlowException(String.format("\u52a0\u8f7d\u914d\u7f6e\u8fc7\u7a0b\u4e2d\u51fa\u73b0\u9519\u8bef,path=%s", location), e);
      }
   }

   public void registry(Flow flow) {
      try {
         flow.setRetryException(this.retryExceptions);
         this.flowEngine.registry(flow);
         logger.info("\u6ce8\u518c\u6d41\u7a0b:[{}\uff3d\u6210\u529f,\u63cf\u8ff0:{},version={}", new Object[]{flow.getName(), flow.getDescription(), flow.getVersion()});
         if (logger.isDebugEnabled()) {
            logger.debug("\u6d41\u7a0b\u8be6\u7ec6\u5b9a\u4e49:{}", JSON.toJSONString(flow));
         }

      } catch (Exception e) {
         if (e instanceof FlowEngineNestException) {
            throw (FlowEngineNestException)e;
         } else {
            throw new FlowException(String.format("\u6ce8\u518c\u6d41\u7a0b\u5b9a\u4e49\u8fc7\u7a0b\u51fa\u9519,Flow=%s,Version=%s", flow.getName(), flow.getVersion()), e);
         }
      }
   }

   public void destroy() throws Exception {
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }
}
