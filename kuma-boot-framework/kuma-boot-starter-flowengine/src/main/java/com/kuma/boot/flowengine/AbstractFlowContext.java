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
         logger.info("流程定义加载配置路径：{}", location);
      }

      try {
         Resource[] resources = this.getResources(location);
         int i = 0;

         for(int j = resources.length; i < j; ++i) {
            Resource resource = resources[i];
            if (logger.isInfoEnabled()) {
               logger.info("加载配置文件:{}", resource.getURL());
            }

            if (!resource.exists()) {
               throw new FlowException(String.format("%s流程定义文件不存在", resource.getURL()));
            }

            this.flowDefXmLDocumentReader.analyze(resource);
         }

      } catch (IOException e) {
         throw new FlowException(String.format("加载配置过程中出现错误,path=%s", location), e);
      }
   }

   public void registry(Flow flow) {
      try {
         flow.setRetryException(this.retryExceptions);
         this.flowEngine.registry(flow);
         logger.info("注册流程:[{}］成功,描述:{},version={}", new Object[]{flow.getName(), flow.getDescription(), flow.getVersion()});
         if (logger.isDebugEnabled()) {
            logger.debug("流程详细定义:{}", JSON.toJSONString(flow));
         }

      } catch (Exception e) {
         if (e instanceof FlowEngineNestException) {
            throw (FlowEngineNestException)e;
         } else {
            throw new FlowException(String.format("注册流程定义过程出错,Flow=%s,Version=%s", flow.getName(), flow.getVersion()), e);
         }
      }
   }

   public void destroy() throws Exception {
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }
}
