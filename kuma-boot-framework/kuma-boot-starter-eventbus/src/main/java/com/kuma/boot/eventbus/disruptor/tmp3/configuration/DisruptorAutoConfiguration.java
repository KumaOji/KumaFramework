package com.kuma.boot.eventbus.disruptor.tmp3.configuration;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.annotation.EventRule;
import com.kuma.boot.eventbus.disruptor.tmp3.context.DisruptorEventAwareProcessor;
import com.kuma.boot.eventbus.disruptor.tmp3.context.DisruptorTemplate;
import com.kuma.boot.eventbus.disruptor.tmp3.context.EventHandlerDefinition;
import com.kuma.boot.eventbus.disruptor.tmp3.context.Ini;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorApplicationEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorBindEventFactory;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEvent;
import com.kuma.boot.eventbus.disruptor.tmp3.event.DisruptorEventThreadFactory;
import com.kuma.boot.eventbus.disruptor.tmp3.handler.DefaultHandlerChainManager;
import com.kuma.boot.eventbus.disruptor.tmp3.handler.DisruptorEventDispatcher;
import com.kuma.boot.eventbus.disruptor.tmp3.handler.DisruptorHandler;
import com.kuma.boot.eventbus.disruptor.tmp3.handler.HandlerChainManager;
import com.kuma.boot.eventbus.disruptor.tmp3.handler.Nameable;
import com.kuma.boot.eventbus.disruptor.tmp3.handler.PathMatchingHandlerChainResolver;
import com.kuma.boot.eventbus.disruptor.tmp3.hooks.DisruptorShutdownHook;
import com.kuma.boot.eventbus.disruptor.tmp3.properties.DisruptorProperties;
import com.kuma.boot.eventbus.disruptor.tmp3.translator.DisruptorEventOneArgTranslator;
import com.kuma.boot.eventbus.disruptor.tmp3.translator.DisruptorEventThreeArgTranslator;
import com.kuma.boot.eventbus.disruptor.tmp3.translator.DisruptorEventTwoArgTranslator;
import com.kuma.boot.eventbus.disruptor.tmp3.util.StringUtils;
import com.kuma.boot.eventbus.disruptor.tmp3.util.WaitStrategys;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.OrderComparator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@AutoConfiguration
@ConditionalOnClass({Disruptor.class})
@EnableConfigurationProperties({DisruptorProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.eventbus.disruptor",
   value = {"enabled"},
   havingValue = "true"
)
public class DisruptorAutoConfiguration implements ApplicationContextAware, InitializingBean {
   private ApplicationContext applicationContext;
   private final Map<String, String> handlerChainDefinitionMap = new HashMap();

   public DisruptorAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(DisruptorAutoConfiguration.class, "kuma-boot-starter-eventbus", new String[0]);
   }

   @Bean
   @ConditionalOnMissingBean
   public WaitStrategy waitStrategy() {
      return WaitStrategys.YIELDING_WAIT;
   }

   @Bean
   @ConditionalOnMissingBean
   public EventFactory<DisruptorEvent> eventFactory() {
      return new DisruptorBindEventFactory();
   }

   @Bean({"disruptorHandlers"})
   public Map<String, DisruptorHandler<DisruptorEvent>> disruptorHandlers() {
      Map<String, DisruptorHandler<DisruptorEvent>> disruptorPreHandlers = new LinkedHashMap();
      Map<String, DisruptorHandler> beansOfType = this.getApplicationContext().getBeansOfType(DisruptorHandler.class);
      if (!ObjectUtils.isEmpty(beansOfType)) {
         for(Map.Entry<String, DisruptorHandler> entry : beansOfType.entrySet()) {
            if (!(entry.getValue() instanceof DisruptorEventDispatcher)) {
               EventRule annotationType = (EventRule)this.getApplicationContext().findAnnotationOnBean((String)entry.getKey(), EventRule.class);
               if (annotationType == null) {
                  LogUtils.error("Not Found AnnotationType {} on Bean {} Whith Name {}", new Object[]{EventRule.class, ((DisruptorHandler)entry.getValue()).getClass(), entry.getKey()});
               } else {
                  this.handlerChainDefinitionMap.put(annotationType.value(), (String)entry.getKey());
               }

               disruptorPreHandlers.put((String)entry.getKey(), (DisruptorHandler)entry.getValue());
            }
         }
      }

      return disruptorPreHandlers;
   }

   @Bean({"disruptorEventHandlers"})
   public List<DisruptorEventDispatcher> disruptorEventHandlers(DisruptorProperties properties, @Qualifier("disruptorHandlers") Map<String, DisruptorHandler<DisruptorEvent>> eventHandlers) {
      List<EventHandlerDefinition> handlerDefinitions = properties.getHandlerDefinitions();
      List<DisruptorEventDispatcher> disruptorEventHandlers = new ArrayList();
      if (CollectionUtils.isEmpty(handlerDefinitions)) {
         EventHandlerDefinition definition = new EventHandlerDefinition();
         definition.setOrder(0);
         definition.setDefinitionMap(this.handlerChainDefinitionMap);
         disruptorEventHandlers.add(this.createDisruptorEventHandler(definition, eventHandlers));
      } else {
         for(EventHandlerDefinition handlerDefinition : handlerDefinitions) {
            disruptorEventHandlers.add(this.createDisruptorEventHandler(handlerDefinition, eventHandlers));
         }
      }

      disruptorEventHandlers.sort(new OrderComparator());
      return disruptorEventHandlers;
   }

   protected DisruptorEventDispatcher createDisruptorEventHandler(EventHandlerDefinition handlerDefinition, Map<String, DisruptorHandler<DisruptorEvent>> eventHandlers) {
      if (StringUtils.isNotEmpty(handlerDefinition.getDefinitions())) {
         this.handlerChainDefinitionMap.putAll(this.parseHandlerChainDefinitions(handlerDefinition.getDefinitions()));
      } else if (!CollectionUtils.isEmpty(handlerDefinition.getDefinitionMap())) {
         this.handlerChainDefinitionMap.putAll(handlerDefinition.getDefinitionMap());
      }

      HandlerChainManager<DisruptorEvent> manager = this.createHandlerChainManager(eventHandlers, this.handlerChainDefinitionMap);
      PathMatchingHandlerChainResolver chainResolver = new PathMatchingHandlerChainResolver();
      chainResolver.setHandlerChainManager(manager);
      return new DisruptorEventDispatcher(chainResolver, handlerDefinition.getOrder());
   }

   protected Map<String, String> parseHandlerChainDefinitions(String definitions) {
      Ini ini = new Ini();
      ini.load(definitions);
      Ini.Section section = ini.getSection("urls");
      if (CollectionUtils.isEmpty(section)) {
         section = ini.getSection("");
      }

      return section;
   }

   protected HandlerChainManager<DisruptorEvent> createHandlerChainManager(Map<String, DisruptorHandler<DisruptorEvent>> eventHandlers, Map<String, String> handlerChainDefinitionMap) {
      HandlerChainManager<DisruptorEvent> manager = new DefaultHandlerChainManager();
      if (!CollectionUtils.isEmpty(eventHandlers)) {
         for(Map.Entry<String, DisruptorHandler<DisruptorEvent>> entry : eventHandlers.entrySet()) {
            String name = (String)entry.getKey();
            DisruptorHandler<DisruptorEvent> handler = (DisruptorHandler)entry.getValue();
            if (handler instanceof Nameable) {
               ((Nameable)handler).setName(name);
            }

            manager.addHandler(name, handler);
         }
      }

      if (!CollectionUtils.isEmpty(handlerChainDefinitionMap)) {
         for(Map.Entry<String, String> entry : handlerChainDefinitionMap.entrySet()) {
            String rule = (String)entry.getKey();
            String chainDefinition = (String)entry.getValue();
            manager.createChain(rule, chainDefinition);
         }
      }

      return manager;
   }

   @Bean
   @ConditionalOnClass({Disruptor.class})
   @ConditionalOnProperty(
      prefix = "kuma.boot.eventbus.disruptor",
      value = {"enabled"},
      havingValue = "true"
   )
   public Disruptor<DisruptorEvent> disruptor(DisruptorProperties properties, WaitStrategy waitStrategy, EventFactory<DisruptorEvent> eventFactory, @Qualifier("disruptorEventHandlers") List<DisruptorEventDispatcher> disruptorEventHandlers) {
      DisruptorEventThreadFactory threadFactory = new DisruptorEventThreadFactory();
      Disruptor<DisruptorEvent> disruptor;
      if (properties.getMultiProducer()) {
         disruptor = new Disruptor(eventFactory, properties.getRingBufferSize(), threadFactory, ProducerType.MULTI, waitStrategy);
      } else {
         disruptor = new Disruptor(eventFactory, properties.getRingBufferSize(), threadFactory, ProducerType.SINGLE, waitStrategy);
      }

      if (!ObjectUtils.isEmpty(disruptorEventHandlers)) {
         disruptorEventHandlers.sort(new OrderComparator());
         EventHandlerGroup<DisruptorEvent> handlerGroup = null;

         for(int i = 0; i < disruptorEventHandlers.size(); ++i) {
            DisruptorEventDispatcher eventHandler = (DisruptorEventDispatcher)disruptorEventHandlers.get(i);
            if (i < 1) {
               handlerGroup = disruptor.handleEventsWith(new EventHandler[]{eventHandler});
            } else {
               handlerGroup.then(new EventHandler[]{eventHandler});
            }
         }
      }

      disruptor.start();
      Runtime.getRuntime().addShutdownHook(new DisruptorShutdownHook(disruptor));
      return disruptor;
   }

   @Bean
   @ConditionalOnMissingBean
   public EventTranslatorOneArg<DisruptorEvent, DisruptorEvent> oneArgEventTranslator() {
      return new DisruptorEventOneArgTranslator();
   }

   @Bean
   @ConditionalOnMissingBean
   public EventTranslatorTwoArg<DisruptorEvent, String, String> twoArgEventTranslator() {
      return new DisruptorEventTwoArgTranslator();
   }

   @Bean
   @ConditionalOnMissingBean
   public EventTranslatorThreeArg<DisruptorEvent, String, String, String> threeArgEventTranslator() {
      return new DisruptorEventThreeArgTranslator();
   }

   @Bean
   public DisruptorTemplate disruptorTemplate(Disruptor<DisruptorEvent> disruptor, EventTranslatorOneArg<DisruptorEvent, DisruptorEvent> oneArgEventTranslator) {
      return new DisruptorTemplate(disruptor, oneArgEventTranslator);
   }

   @Bean
   public ApplicationListener<DisruptorApplicationEvent> disruptorEventListener(Disruptor<DisruptorEvent> disruptor, EventTranslatorOneArg<DisruptorEvent, DisruptorEvent> oneArgEventTranslator) {
      return (appEvent) -> {
         DisruptorEvent event = (DisruptorEvent)appEvent.getSource();
         disruptor.publishEvent(oneArgEventTranslator, event);
      };
   }

   @Bean
   public DisruptorEventAwareProcessor disruptorEventAwareProcessor() {
      return new DisruptorEventAwareProcessor();
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   public ApplicationContext getApplicationContext() {
      return this.applicationContext;
   }
}
