package com.kuma.boot.flowengine.simpleflow.core.config;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowEngine;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowResult;
import com.kuma.boot.flowengine.simpleflow.core.engine.DefaultFlowEngine;
import com.kuma.boot.flowengine.simpleflow.core.executor.StandaloneStepExecutorRegistry;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.yaml.snakeyaml.Yaml;

public class StandaloneFlowConfiguration {
   private final FlowEngine flowEngine;
   private final StandaloneFlowConfigurationParser parser;
   private final Map<String, FlowDefinition> flowDefinitions = new HashMap();
   private final ExecutorService executorService;
   private ThreadPoolConfig threadPoolConfig;

   public StandaloneFlowConfiguration() {
      this.threadPoolConfig = new ThreadPoolConfig();
      this.executorService = this.threadPoolConfig.createThreadPoolExecutor();
      this.flowEngine = new DefaultFlowEngine(this.executorService);
      this.parser = new StandaloneFlowConfigurationParser();
   }

   public StandaloneFlowConfiguration(ExecutorService executorService) {
      this.executorService = executorService;
      this.flowEngine = new DefaultFlowEngine(executorService);
      this.parser = new StandaloneFlowConfigurationParser();
   }

   public StandaloneFlowConfiguration(ThreadPoolConfig threadPoolConfig) {
      this.threadPoolConfig = threadPoolConfig;
      this.executorService = threadPoolConfig.createThreadPoolExecutor();
      this.flowEngine = new DefaultFlowEngine(this.executorService);
      this.parser = new StandaloneFlowConfigurationParser();
   }

   public void loadFromYaml(String yamlFilePath) {
      System.out.println("loadFromYaml called with: " + yamlFilePath);

      try {
         InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(yamlFilePath);

         try {
            System.out.println("InputStream: " + String.valueOf(inputStream));
            if (inputStream == null) {
               throw new IllegalArgumentException("YAML file not found: " + yamlFilePath);
            }

            Yaml yaml = new Yaml();
            Map<String, Object> config = (Map)yaml.load(inputStream);
            System.out.println("Loaded config: " + String.valueOf(config));
            LogUtils.info("Loaded YAML config: {}", new Object[]{config});
            this.parseConfiguration(config);
            LogUtils.info("Successfully loaded flow configurations from: {}", new Object[]{yamlFilePath});
         } catch (Throwable var6) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (inputStream != null) {
            inputStream.close();
         }

      } catch (Exception e) {
         System.out.println("Exception in loadFromYaml: " + e.getMessage());
         LogUtils.error("Failed to load flow configurations from: {}", new Object[]{yamlFilePath, e});
         e.printStackTrace();
         throw new RuntimeException("Failed to load flow configurations", e);
      }
   }

   public void loadFromConfig(Map<String, Object> config) {
      this.parseConfiguration(config);
   }

   private void parseConfiguration(Map<String, Object> config) {
      System.out.println("parseConfiguration called with config: " + String.valueOf(config));
      LogUtils.info("Parsing configuration: {}", new Object[]{config.keySet()});
      Map<String, Object> simpleFlowConfig = (Map)config.get("simple-flow");
      if (simpleFlowConfig == null) {
         LogUtils.warn("No 'simple-flow' configuration found", new Object[0]);
      } else {
         ThreadPoolConfig parsedThreadPoolConfig = ThreadPoolConfig.parseFromConfig(simpleFlowConfig);
         if (parsedThreadPoolConfig != null) {
            this.threadPoolConfig = parsedThreadPoolConfig;
         }

         LogUtils.info("Simple flow config keys: {}", new Object[]{simpleFlowConfig.keySet()});
         List<Map<String, Object>> flows = (List)simpleFlowConfig.get("flows");
         if (flows != null && !flows.isEmpty()) {
            LogUtils.info("Found {} flows to parse", new Object[]{flows.size()});

            for(Map<String, Object> flowConfig : flows) {
               try {
                  LogUtils.info("Parsing flow config: {}", new Object[]{flowConfig});
                  FlowDefinition flowDefinition = this.parser.parseFlow(flowConfig);
                  this.flowDefinitions.put(flowDefinition.getId(), flowDefinition);
                  LogUtils.info("Successfully loaded flow: {}", new Object[]{flowDefinition.getId()});
               } catch (Exception e) {
                  LogUtils.error("Failed to parse flow configuration: {}", new Object[]{flowConfig.get("id"), e});
               }
            }

         } else {
            LogUtils.warn("No flows configuration found", new Object[0]);
         }
      }
   }

   public void registerStepExecutor(String stepType, Class<?> executorClass) {
      StandaloneStepExecutorRegistry.getInstance().registerExecutor(stepType, executorClass);
   }

   public void registerBean(String beanName, Object beanInstance) {
      StandaloneStepExecutorRegistry.getInstance().registerBean(beanName, beanInstance);
   }

   public FlowResult executeFlow(String flowId, Map<String, Object> context) {
      LogUtils.info("Available flows: {}", new Object[]{this.flowDefinitions.keySet()});
      FlowDefinition flowDefinition = (FlowDefinition)this.flowDefinitions.get(flowId);
      if (flowDefinition == null) {
         throw new IllegalArgumentException("Flow not found: " + flowId);
      } else {
         LogUtils.info("Executing flow: {} with context: {}", new Object[]{flowId, context});
         return this.flowEngine.execute((FlowDefinition)flowDefinition, (Map)(context != null ? context : new HashMap()));
      }
   }

   public FlowResult executeFlow(String flowId) {
      return this.executeFlow(flowId, new HashMap());
   }

   public Map<String, FlowDefinition> getFlowDefinitions() {
      return new HashMap(this.flowDefinitions);
   }

   public boolean hasFlow(String flowId) {
      return this.flowDefinitions.containsKey(flowId);
   }

   public FlowEngine getFlowEngine() {
      return this.flowEngine;
   }

   public ThreadPoolConfig getThreadPoolConfig() {
      return this.threadPoolConfig;
   }

   public void shutdown() {
      if (this.executorService != null && !this.executorService.isShutdown()) {
         this.executorService.shutdown();
         LogUtils.info("StandaloneFlowConfiguration shutdown completed", new Object[0]);
      }

   }
}
