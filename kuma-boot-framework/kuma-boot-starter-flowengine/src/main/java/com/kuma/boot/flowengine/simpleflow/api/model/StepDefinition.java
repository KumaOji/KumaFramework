package com.kuma.boot.flowengine.simpleflow.api.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class StepDefinition {
   private String id;
   private String name;
   private String description;
   private StepType type;
   private String executorClass;
   private Map<String, Object> parameters;
   private Map<String, Object> properties;
   private boolean retryEnabled;
   private int maxRetries;
   private long retryDelayMs;
   private long timeoutMs;
   private boolean skipOnError;
   private String condition;
   private List<StepDefinition> subSteps;
   private Map<String, String> inputMappings;
   private Map<String, String> outputMappings;
   private ParallelConfig parallelConfig;

   public StepDefinition() {
   }

   public <T> Optional<T> getParameter(String key) {
      return Optional.ofNullable(this.parameters.get(key));
   }

   public <T> T getParameter(String key, T defaultValue) {
      return (T)this.parameters.getOrDefault(key, defaultValue);
   }

   public <T> Optional<T> getProperty(String key) {
      return Optional.ofNullable(this.properties.get(key));
   }

   public <T> T getProperty(String key, T defaultValue) {
      return (T)this.properties.getOrDefault(key, defaultValue);
   }

   public boolean hasCondition() {
      return this.condition != null && !this.condition.trim().isEmpty();
   }

   public boolean hasSubSteps() {
      return !this.subSteps.isEmpty();
   }

   public boolean isParallel() {
      return this.type == StepDefinition.StepType.PARALLEL;
   }

   public boolean isConditional() {
      return this.type == StepDefinition.StepType.CONDITIONAL || this.hasCondition();
   }

   public boolean isLoop() {
      return this.type == StepDefinition.StepType.LOOP;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         StepDefinition that = (StepDefinition)o;
         return Objects.equals(this.id, that.id);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id});
   }

   public String toString() {
      String var10000 = this.id;
      return "StepDefinition{id='" + var10000 + "', name='" + this.name + "', type=" + String.valueOf(this.type) + ", executorClass='" + this.executorClass + "'}";
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public StepType getType() {
      return this.type;
   }

   public void setType(StepType type) {
      this.type = type;
   }

   public String getExecutorClass() {
      return this.executorClass;
   }

   public void setExecutorClass(String executorClass) {
      this.executorClass = executorClass;
   }

   public Map<String, Object> getParameters() {
      return this.parameters;
   }

   public void setParameters(Map<String, Object> parameters) {
      this.parameters = parameters;
   }

   public Map<String, Object> getProperties() {
      return this.properties;
   }

   public void setProperties(Map<String, Object> properties) {
      this.properties = properties;
   }

   public boolean isRetryEnabled() {
      return this.retryEnabled;
   }

   public void setRetryEnabled(boolean retryEnabled) {
      this.retryEnabled = retryEnabled;
   }

   public int getMaxRetries() {
      return this.maxRetries;
   }

   public void setMaxRetries(int maxRetries) {
      this.maxRetries = maxRetries;
   }

   public long getRetryDelayMs() {
      return this.retryDelayMs;
   }

   public void setRetryDelayMs(long retryDelayMs) {
      this.retryDelayMs = retryDelayMs;
   }

   public long getTimeoutMs() {
      return this.timeoutMs;
   }

   public void setTimeoutMs(long timeoutMs) {
      this.timeoutMs = timeoutMs;
   }

   public boolean isSkipOnError() {
      return this.skipOnError;
   }

   public void setSkipOnError(boolean skipOnError) {
      this.skipOnError = skipOnError;
   }

   public String getCondition() {
      return this.condition;
   }

   public void setCondition(String condition) {
      this.condition = condition;
   }

   public List<StepDefinition> getSubSteps() {
      return this.subSteps;
   }

   public void setSubSteps(List<StepDefinition> subSteps) {
      this.subSteps = subSteps;
   }

   public Map<String, String> getInputMappings() {
      return this.inputMappings;
   }

   public void setInputMappings(Map<String, String> inputMappings) {
      this.inputMappings = inputMappings;
   }

   public Map<String, String> getOutputMappings() {
      return this.outputMappings;
   }

   public void setOutputMappings(Map<String, String> outputMappings) {
      this.outputMappings = outputMappings;
   }

   public ParallelConfig getParallelConfig() {
      return this.parallelConfig;
   }

   public void setParallelConfig(ParallelConfig parallelConfig) {
      this.parallelConfig = parallelConfig;
   }

   public static StepDefinitionBuilder builder() {
      return new StepDefinitionBuilder();
   }

   public static enum StepType {
      SIMPLE,
      CONDITIONAL,
      PARALLEL,
      LOOP,
      SCRIPT,
      SCRIPT_CONDITIONAL,
      SERVICE,
      HUMAN_TASK,
      TIMER,
      GATEWAY;

      private StepType() {
      }

      // $FF: synthetic method
      private static StepType[] $values() {
         return new StepType[]{SIMPLE, CONDITIONAL, PARALLEL, LOOP, SCRIPT, SCRIPT_CONDITIONAL, SERVICE, HUMAN_TASK, TIMER, GATEWAY};
      }
   }

   public static final class StepDefinitionBuilder {
      private String id;
      private String name;
      private String description;
      private StepType type;
      private String executorClass;
      private Map<String, Object> parameters;
      private Map<String, Object> properties;
      private boolean retryEnabled;
      private int maxRetries;
      private long retryDelayMs;
      private long timeoutMs;
      private boolean skipOnError;
      private String condition;
      private List<StepDefinition> subSteps;
      private Map<String, String> inputMappings;
      private Map<String, String> outputMappings;
      private ParallelConfig parallelConfig;

      private StepDefinitionBuilder() {
      }

      public StepDefinitionBuilder id(String id) {
         this.id = id;
         return this;
      }

      public StepDefinitionBuilder name(String name) {
         this.name = name;
         return this;
      }

      public StepDefinitionBuilder description(String description) {
         this.description = description;
         return this;
      }

      public StepDefinitionBuilder type(StepType type) {
         this.type = type;
         return this;
      }

      public StepDefinitionBuilder executorClass(String executorClass) {
         this.executorClass = executorClass;
         return this;
      }

      public StepDefinitionBuilder parameters(Map<String, Object> parameters) {
         this.parameters = parameters;
         return this;
      }

      public StepDefinitionBuilder properties(Map<String, Object> properties) {
         this.properties = properties;
         return this;
      }

      public StepDefinitionBuilder retryEnabled(boolean retryEnabled) {
         this.retryEnabled = retryEnabled;
         return this;
      }

      public StepDefinitionBuilder maxRetries(int maxRetries) {
         this.maxRetries = maxRetries;
         return this;
      }

      public StepDefinitionBuilder retryDelayMs(long retryDelayMs) {
         this.retryDelayMs = retryDelayMs;
         return this;
      }

      public StepDefinitionBuilder timeoutMs(long timeoutMs) {
         this.timeoutMs = timeoutMs;
         return this;
      }

      public StepDefinitionBuilder skipOnError(boolean skipOnError) {
         this.skipOnError = skipOnError;
         return this;
      }

      public StepDefinitionBuilder condition(String condition) {
         this.condition = condition;
         return this;
      }

      public StepDefinitionBuilder subSteps(List<StepDefinition> subSteps) {
         this.subSteps = subSteps;
         return this;
      }

      public StepDefinitionBuilder inputMappings(Map<String, String> inputMappings) {
         this.inputMappings = inputMappings;
         return this;
      }

      public StepDefinitionBuilder outputMappings(Map<String, String> outputMappings) {
         this.outputMappings = outputMappings;
         return this;
      }

      public StepDefinitionBuilder parallelConfig(ParallelConfig parallelConfig) {
         this.parallelConfig = parallelConfig;
         return this;
      }

      public StepDefinition build() {
         StepDefinition stepDefinition = new StepDefinition();
         stepDefinition.setId(this.id);
         stepDefinition.setName(this.name);
         stepDefinition.setDescription(this.description);
         stepDefinition.setType(this.type);
         stepDefinition.setExecutorClass(this.executorClass);
         stepDefinition.setParameters(this.parameters);
         stepDefinition.setProperties(this.properties);
         stepDefinition.setRetryEnabled(this.retryEnabled);
         stepDefinition.setMaxRetries(this.maxRetries);
         stepDefinition.setRetryDelayMs(this.retryDelayMs);
         stepDefinition.setTimeoutMs(this.timeoutMs);
         stepDefinition.setSkipOnError(this.skipOnError);
         stepDefinition.setCondition(this.condition);
         stepDefinition.setSubSteps(this.subSteps);
         stepDefinition.setInputMappings(this.inputMappings);
         stepDefinition.setOutputMappings(this.outputMappings);
         stepDefinition.setParallelConfig(this.parallelConfig);
         return stepDefinition;
      }
   }
}
