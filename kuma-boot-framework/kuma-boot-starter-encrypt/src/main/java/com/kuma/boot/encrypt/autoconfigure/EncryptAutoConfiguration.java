package com.kuma.boot.encrypt.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.annotation.SignEncrypt;
import com.kuma.boot.encrypt.autoconfigure.properties.EncryptFilterProperties;
import com.kuma.boot.encrypt.autoconfigure.properties.EncryptProperties;
import com.kuma.boot.encrypt.enums.EncryptType;
import com.kuma.boot.encrypt.exception.EncryptException;
import com.kuma.boot.encrypt.handler.EncryptHandler;
import com.kuma.boot.encrypt.handler.SignEncryptHandler;
import com.kuma.boot.encrypt.handler.impl.AesEncryptHandler;
import com.kuma.boot.encrypt.handler.impl.Base64EncryptHandler;
import com.kuma.boot.encrypt.handler.impl.RsaEncryptHandler;
import com.kuma.boot.encrypt.handler.impl.SignEncryptHandlerImpl;
import com.kuma.boot.encrypt.interceptor.SignEncryptInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@AutoConfiguration
@EnableConfigurationProperties({EncryptProperties.class, EncryptFilterProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.encrypt",
   name = {"enabled"},
   havingValue = "true"
)
public class EncryptAutoConfiguration implements ApplicationContextAware, BeanFactoryPostProcessor, EnvironmentAware, InitializingBean {
   private ApplicationContext applicationContext;
   private Environment environment;

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(EncryptAutoConfiguration.class, "kuma-boot-starter-encrypt", new String[0]);
   }

   public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
      DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)configurableListableBeanFactory;
      GenericBeanDefinition bean = new GenericBeanDefinition();
      EncryptType type = (EncryptType)this.environment.getProperty("encrypt.type", EncryptType.class);
      String secret = (String)this.environment.getProperty("encrypt.secret", String.class);
      String publicKey = (String)this.environment.getProperty("encrypt.publicKey", String.class);
      String privateKey = (String)this.environment.getProperty("encrypt.privateKey", String.class);
      Boolean debug = (Boolean)this.environment.getProperty("encrypt.debug", Boolean.TYPE);
      if (debug == null || !debug) {
         if (type == null) {
            throw new EncryptException("没有定义加密类型(No encryption type is defined)");
         } else {
            switch (type) {
               case BASE64:
                  bean.setBeanClass(Base64EncryptHandler.class);
                  bean.setPrimary(true);
                  beanFactory.registerBeanDefinition("encryptHandler", bean);
                  break;
               case AES:
                  if (secret == null || "".equals(secret.trim())) {
                     throw new EncryptException("没有定义秘钥(No secret key is defined)");
                  }

                  bean.setBeanClass(AesEncryptHandler.class);
                  bean.getPropertyValues().add("secret", secret);
                  bean.setPrimary(true);
                  beanFactory.registerBeanDefinition("encryptHandler", bean);
                  break;
               case RSA:
                  if (publicKey == null || "".equals(publicKey.trim())) {
                     throw new EncryptException("没有定义公钥(No publicKey is defined)");
                  }

                  if (privateKey == null || "".equals(privateKey.trim())) {
                     throw new EncryptException("没有定义私钥(No privateKey is defined)");
                  }

                  bean.setBeanClass(RsaEncryptHandler.class);
                  bean.getPropertyValues().add("publicKey", publicKey);
                  bean.getPropertyValues().add("privateKey", privateKey);
                  bean.setPrimary(true);
                  beanFactory.registerBeanDefinition("encryptHandler", bean);
                  break;
               case CUSTOM:
                  try {
                     beanFactory.getBean(EncryptHandler.class);
                  } catch (Exception var10) {
                     throw new EncryptException("没有自定义加密处理器(No custom encryption processor)");
                  }
            }

         }
      }
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   public void setEnvironment(Environment environment) {
      this.environment = environment;
   }

   @Configuration
   @ConditionalOnExpression("environment.getProperty('encrypt.signSecret')!=null && environment.getProperty('encrypt.signSecret').trim()!=''")
   public static class SignEncryptConfiguration implements InitializingBean {
      private final SignEncryptHandler signEncryptHandler;

      public SignEncryptConfiguration(SignEncryptHandler signEncryptHandler) {
         this.signEncryptHandler = signEncryptHandler;
      }

      public void afterPropertiesSet() throws Exception {
         LogUtils.started(SignEncryptConfiguration.class, "kuma-boot-starter-encrypt", new String[0]);
      }

      @Bean
      @ConditionalOnMissingBean({SignEncryptHandler.class})
      public SignEncryptHandler sortSignEncryptHandlerDefult() {
         return new SignEncryptHandlerImpl();
      }

      @Bean
      public DefaultPointcutAdvisor sortSignEncryptAdvisor(@Value("${encrypt.signSecret}") String sortSignSecret) {
         SignEncryptInterceptor interceptor = new SignEncryptInterceptor(sortSignSecret, this.signEncryptHandler);
         AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut((Class)null, SignEncrypt.class);
         DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
         advisor.setPointcut(pointcut);
         advisor.setAdvice(interceptor);
         return advisor;
      }
   }
}
