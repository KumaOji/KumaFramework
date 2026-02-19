/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.kafka.clients.producer.KafkaProducer
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.boot.autoconfigure.AutoConfigurationPackages
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.mq.kafka.kafkafactory;

import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(value={KafkaProducerProperties.class})
public class MqAutoConfiguration
implements InitializingBean {
    @Autowired
    private KafkaProducerProperties kafkaProducerProperties;

    @Bean
    @ConditionalOnMissingBean
    public KafkaProducer<String, String> kafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", this.kafkaProducerProperties.getBootstrapServers());
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("client.id", this.kafkaProducerProperties.getClientId());
        props.put("acks", this.kafkaProducerProperties.getAcks());
        return new KafkaProducer(props);
    }

    public void afterPropertiesSet() throws Exception {
    }

    @Configuration
    @Import(value={ProducerRegister.class})
    public static class MapperScannerRegistrarConfiguration
    implements InitializingBean {
        public void afterPropertiesSet() throws Exception {
        }
    }

    public static class ProducerRegister
    implements BeanFactoryAware,
    ImportBeanDefinitionRegistrar {
        private BeanFactory beanFactory;

        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
            try {
                Resource[] resources;
                if (!AutoConfigurationPackages.has((BeanFactory)this.beanFactory)) {
                    return;
                }
                List packages = AutoConfigurationPackages.get((BeanFactory)this.beanFactory);
                String basePackage = StringUtils.collectionToCommaDelimitedString((Collection)packages);
                String packageSearchPath = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";
                PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
                for (Resource resource : resources = resourcePatternResolver.getResources(packageSearchPath)) {
                }
            }
            catch (Exception e) {
                LogUtils.error((Throwable)e);
            }
        }
    }
}

