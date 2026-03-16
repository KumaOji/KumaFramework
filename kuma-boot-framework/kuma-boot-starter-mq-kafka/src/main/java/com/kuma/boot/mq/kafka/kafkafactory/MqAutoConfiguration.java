package com.kuma.boot.mq.kafka.kafkafactory;

import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Properties;

/**
 * (
 *
 * @Producerpublic interface OrderProducer {
 * <p>
 * RecordMetadata send(String topic, String userInfo);
 * <p>
 * }
 */
@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
@ConditionalOnProperty(prefix = "mq.kafka.producer", name = "bootstrap-servers")
public class MqAutoConfiguration implements InitializingBean {

    @Autowired
    private KafkaProducerProperties kafkaProducerProperties;

    @Bean
    @ConditionalOnMissingBean
    public KafkaProducer<String, String> kafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerProperties.getClientId());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerProperties.getAcks());
        return new KafkaProducer<>(props);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public static class ProducerRegister implements BeanFactoryAware, ImportBeanDefinitionRegistrar {

        private BeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
            try {
                if (!AutoConfigurationPackages.has(this.beanFactory)) {
                    return;
                }
                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                String basePackage = StringUtils.collectionToCommaDelimitedString(packages);

                String packageSearchPath = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";

                ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

                for (Resource resource : resources) {
                    //todo
                    //MetadataReader metadataReader = new SimpleMetadataReader(resource, ClassUtils.getDefaultClassLoader());
                    //Producer annotation = Class.forName(metadataReader.getClassMetadata().getClassName()).getAnnotation(Producer.class);
                    //if (null == annotation) continue;
                    //ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
                    //String beanName = Introspector.decapitalize(ClassUtils.getShortName(beanDefinition.getBeanClassName()));
                    //beanDefinition.setResource(resource);
                    //beanDefinition.setSource(resource);
                    //beanDefinition.setScope("singleton");
                    //beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName());
                    //beanDefinition.setBeanClass(KafkaProducerFactoryBean.class);
                    //BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
                    //registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
                }

            } catch (Exception e) {
                LogUtils.error(e);
            }
        }

    }

    @Configuration
    @Import(ProducerRegister.class)
    public static class MapperScannerRegistrarConfiguration implements InitializingBean {

        @Override
        public void afterPropertiesSet() throws Exception {

        }
    }

}
