package com.kuma.boot.flowengine.simpleflow.api.model;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 步骤定义模型
 * <p>
 * 定义工作流程中的单个步骤
 *
 * @author Simple Flow Team
 * @since 1.0.0
 */
public class StepDefinition {

   /**
    * 步骤类型枚举
    */
   public enum StepType {
      SIMPLE,      // 简单步骤
      CONDITIONAL, // 条件步骤
      PARALLEL,    // 并行步骤
      LOOP,        // 循环步骤
      SCRIPT,      // 脚本步骤
      SCRIPT_CONDITIONAL, // 脚本条件步骤
      SERVICE,     // 服务调用步骤
      HUMAN_TASK,  // 人工任务步骤
      TIMER,       // 定时器步骤
      GATEWAY      // 网关步骤
   }

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

   /**
    * 获取指定参数值
    */
   @SuppressWarnings("unchecked")
   public <T> Optional<T> getParameter(String key) {
      return Optional.ofNullable((T) parameters.get(key));
   }

   /**
    * 获取指定参数值，如果不存在则返回默认值
    */
   @SuppressWarnings("unchecked")
   public <T> T getParameter(String key, T defaultValue) {
      return (T) parameters.getOrDefault(key, defaultValue);
   }

   /**
    * 获取指定属性值
    */
   @SuppressWarnings("unchecked")
   public <T> Optional<T> getProperty(String key) {
      return Optional.ofNullable((T) properties.get(key));
   }

   /**
    * 获取指定属性值，如果不存在则返回默认值
    */
   @SuppressWarnings("unchecked")
   public <T> T getProperty(String key, T defaultValue) {
      return (T) properties.getOrDefault(key, defaultValue);
   }

   /**
    * 检查是否有条件
    */
   public boolean hasCondition() {
      return condition != null && !condition.trim().isEmpty();
   }

   /**
    * 检查是否有子步骤
    */
   public boolean hasSubSteps() {
      return !subSteps.isEmpty();
   }

   /**
    * 检查是否为并行步骤
    */
   public boolean isParallel() {
      return type == StepType.PARALLEL;
   }

   /**
    * 检查是否为条件步骤
    */
   public boolean isConditional() {
      return type == StepType.CONDITIONAL || hasCondition();
   }

   /**
    * 检查是否为循环步骤
    */
   public boolean isLoop() {
      return type == StepType.LOOP;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      StepDefinition that = (StepDefinition) o;
      return Objects.equals(id, that.id);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id);
   }

   @Override
   public String toString() {
      return "StepDefinition{" +
              "id='" + id + '\'' +
              ", name='" + name + '\'' +
              ", type=" + type +
              ", executorClass='" + executorClass + '\'' +
              '}';
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public StepType getType() {
      return type;
   }

   public void setType(StepType type) {
      this.type = type;
   }

   public String getExecutorClass() {
      return executorClass;
   }

   public void setExecutorClass(String executorClass) {
      this.executorClass = executorClass;
   }

   public Map<String, Object> getParameters() {
      return parameters;
   }

   public void setParameters(Map<String, Object> parameters) {
      this.parameters = parameters;
   }

   public Map<String, Object> getProperties() {
      return properties;
   }

   public void setProperties(Map<String, Object> properties) {
      this.properties = properties;
   }

   public boolean isRetryEnabled() {
      return retryEnabled;
   }

   public void setRetryEnabled(boolean retryEnabled) {
      this.retryEnabled = retryEnabled;
   }

   public int getMaxRetries() {
      return maxRetries;
   }

   public void setMaxRetries(int maxRetries) {
      this.maxRetries = maxRetries;
   }

   public long getRetryDelayMs() {
      return retryDelayMs;
   }

   public void setRetryDelayMs(long retryDelayMs) {
      this.retryDelayMs = retryDelayMs;
   }

   public long getTimeoutMs() {
      return timeoutMs;
   }

   public void setTimeoutMs(long timeoutMs) {
      this.timeoutMs = timeoutMs;
   }

   public boolean isSkipOnError() {
      return skipOnError;
   }

   public void setSkipOnError(boolean skipOnError) {
      this.skipOnError = skipOnError;
   }

   public String getCondition() {
      return condition;
   }

   public void setCondition(String condition) {
      this.condition = condition;
   }

   public List<StepDefinition> getSubSteps() {
      return subSteps;
   }

   public void setSubSteps(List<StepDefinition> subSteps) {
      this.subSteps = subSteps;
   }

   public Map<String, String> getInputMappings() {
      return inputMappings;
   }

   public void setInputMappings(Map<String, String> inputMappings) {
      this.inputMappings = inputMappings;
   }

   public Map<String, String> getOutputMappings() {
      return outputMappings;
   }

   public void setOutputMappings(Map<String, String> outputMappings) {
      this.outputMappings = outputMappings;
   }

   public ParallelConfig getParallelConfig() {
      return parallelConfig;
   }

   public void setParallelConfig( ParallelConfig parallelConfig) {
      this.parallelConfig = parallelConfig;
   }

   public static StepDefinitionBuilder builder() {
      return new StepDefinitionBuilder();
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

      public StepDefinitionBuilder parallelConfig( ParallelConfig parallelConfig) {
         this.parallelConfig = parallelConfig;
         return this;
      }

      public StepDefinition build() {
         StepDefinition stepDefinition = new StepDefinition();
         stepDefinition.setId(id);
         stepDefinition.setName(name);
         stepDefinition.setDescription(description);
         stepDefinition.setType(type);
         stepDefinition.setExecutorClass(executorClass);
         stepDefinition.setParameters(parameters);
         stepDefinition.setProperties(properties);
         stepDefinition.setRetryEnabled(retryEnabled);
         stepDefinition.setMaxRetries(maxRetries);
         stepDefinition.setRetryDelayMs(retryDelayMs);
         stepDefinition.setTimeoutMs(timeoutMs);
         stepDefinition.setSkipOnError(skipOnError);
         stepDefinition.setCondition(condition);
         stepDefinition.setSubSteps(subSteps);
         stepDefinition.setInputMappings(inputMappings);
         stepDefinition.setOutputMappings(outputMappings);
         stepDefinition.setParallelConfig(parallelConfig);
         return stepDefinition;
      }
   }
}
