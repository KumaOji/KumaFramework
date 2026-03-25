package com.kuma.boot.openapi.client.proxy;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.openapi.client.annotation.OpenApiRef;
import com.kuma.boot.openapi.common.exception.OpenApiClientException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

@Component
@ConditionalOnProperty({"kuma.boot.openapi.client.config.openApiRefPath"})
public class OpenApiRefProxyRegistry implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware, EnvironmentAware {
   private Environment environment;
   private ResourcePatternResolver resolver;
   private MetadataReaderFactory metadataReaderFactory;

   public void setEnvironment(Environment environment) {
      this.environment = environment;
   }

   public void setResourceLoader(ResourceLoader resourceLoader) {
      this.resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
      this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
   }

   public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
      Set<Class<?>> interClazzSet = this.getInterfacesAnnotatedWith(OpenApiRef.class);
      this.registerOpenApiRefProxies(registry, interClazzSet);
   }

   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
   }

   private Set getInterfacesAnnotatedWith(Class aClass) {
      String scanPath = this.environment.getProperty("kuma.boot.openapi.client.config.openApiRefPath");
      if (StrUtil.isBlank(scanPath)) {
         throw new OpenApiClientException("OpenApiRef接口所在路径为空");
      } else {
         Set<Class<?>> classes = new HashSet();

         try {
            String packageSearchPath = "classpath*:".concat(ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPath)).concat("/**/*.class"));
            Resource[] resources = this.resolver.getResources(packageSearchPath);

            for(Resource resource : resources) {
               if (resource.isReadable()) {
                  MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                  if (metadataReader.getClassMetadata().isInterface()) {
                     Class interClass = Class.forName(metadataReader.getClassMetadata().getClassName());
                     if (interClass.isAnnotationPresent(aClass)) {
                        classes.add(interClass);
                     }
                  }
               }
            }

            return classes;
         } catch (Exception ex) {
            LogUtils.error(String.format("扫描%s下的OpenApiRef接口信息异常", scanPath), new Object[]{ex});
            return classes;
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void registerOpenApiRefProxies(BeanDefinitionRegistry registry, Set interClazzSet) {
      for(Class<?> interClazz : (Set<Class<?>>) interClazzSet) {
         BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(interClazz);
         GenericBeanDefinition definition = (GenericBeanDefinition)beanDefinitionBuilder.getRawBeanDefinition();
         definition.getConstructorArgumentValues().addGenericArgumentValue(interClazz);
         definition.setBeanClass(OpenApiRefProxyFactoryBean.class);
         definition.setAutowireMode(2);
         registry.registerBeanDefinition(interClazz.getName(), definition);
      }

   }
}
