package com.kuma.boot.flowengine.simpleflow.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class FlowDefinition {
   private String id;
   private String name;
   private String description;
   private String version;
   private List<StepDefinition> steps;
   private Map<String, List<String>> dependencies;
   private Map<String, Object> properties;
   private LocalDateTime createdTime;
   private LocalDateTime updatedTime;
   private String threadPoolName;
   private boolean sync;

   public FlowDefinition() {
   }

   public Optional<StepDefinition> findStep(String stepId) {
      return this.steps.stream().filter((step) -> Objects.equals(step.getId(), stepId)).findFirst();
   }

   public List<String> getStepDependencies(String stepId) {
      return (List)this.dependencies.getOrDefault(stepId, Collections.emptyList());
   }

   public List<String> getStepSuccessors(String stepId) {
      return (List)this.dependencies.entrySet().stream().filter((entry) -> ((List)entry.getValue()).contains(stepId)).map(Map.Entry::getKey).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   public boolean hasCyclicDependency() {
      Set<String> visited = new HashSet();
      Set<String> recursionStack = new HashSet();

      for(StepDefinition step : this.steps) {
         if (this.hasCyclicDependencyUtil(step.getId(), visited, recursionStack)) {
            return true;
         }
      }

      return false;
   }

   private boolean hasCyclicDependencyUtil(String stepId, Set<String> visited, Set<String> recursionStack) {
      if (recursionStack.contains(stepId)) {
         return true;
      } else if (visited.contains(stepId)) {
         return false;
      } else {
         visited.add(stepId);
         recursionStack.add(stepId);

         for(String successor : this.getStepSuccessors(stepId)) {
            if (this.hasCyclicDependencyUtil(successor, visited, recursionStack)) {
               return true;
            }
         }

         recursionStack.remove(stepId);
         return false;
      }
   }

   public List<String> getTopologicalOrder() {
      Map<String, Integer> inDegree = new HashMap();
      Queue<String> queue = new LinkedList();
      List<String> result = new ArrayList();

      for(StepDefinition step : this.steps) {
         inDegree.put(step.getId(), 0);
      }

      for(Map.Entry<String, List<String>> entry : this.dependencies.entrySet()) {
         for(String dependency : (List)entry.getValue()) {
            inDegree.put((String)entry.getKey(), (Integer)inDegree.get(entry.getKey()) + 1);
         }
      }

      for(Map.Entry<String, Integer> entry : inDegree.entrySet()) {
         if ((Integer)entry.getValue() == 0) {
            queue.offer((String)entry.getKey());
         }
      }

      while(!queue.isEmpty()) {
         String current = (String)queue.poll();
         result.add(current);

         for(String successor : this.getStepSuccessors(current)) {
            inDegree.put(successor, (Integer)inDegree.get(successor) - 1);
            if ((Integer)inDegree.get(successor) == 0) {
               queue.offer(successor);
            }
         }
      }

      return result;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FlowDefinition that = (FlowDefinition)o;
         return Objects.equals(this.id, that.id) && Objects.equals(this.version, that.version);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.version});
   }

   public String toString() {
      String var10000 = this.id;
      return "FlowDefinition{id='" + var10000 + "', name='" + this.name + "', version='" + this.version + "', steps=" + this.steps.size() + ", threadPoolName='" + this.threadPoolName + "', sync=" + this.sync + "}";
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

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public List<StepDefinition> getSteps() {
      return this.steps;
   }

   public void setSteps(List<StepDefinition> steps) {
      this.steps = steps;
   }

   public Map<String, List<String>> getDependencies() {
      return this.dependencies;
   }

   public void setDependencies(Map<String, List<String>> dependencies) {
      this.dependencies = dependencies;
   }

   public Map<String, Object> getProperties() {
      return this.properties;
   }

   public void setProperties(Map<String, Object> properties) {
      this.properties = properties;
   }

   public LocalDateTime getCreatedTime() {
      return this.createdTime;
   }

   public void setCreatedTime(LocalDateTime createdTime) {
      this.createdTime = createdTime;
   }

   public LocalDateTime getUpdatedTime() {
      return this.updatedTime;
   }

   public void setUpdatedTime(LocalDateTime updatedTime) {
      this.updatedTime = updatedTime;
   }

   public String getThreadPoolName() {
      return this.threadPoolName;
   }

   public void setThreadPoolName(String threadPoolName) {
      this.threadPoolName = threadPoolName;
   }

   public boolean isSync() {
      return this.sync;
   }

   public void setSync(boolean sync) {
      this.sync = sync;
   }

   public static FlowDefinitionBuilder builder() {
      return new FlowDefinitionBuilder();
   }

   public static final class FlowDefinitionBuilder {
      private String id;
      private String name;
      private String description;
      private String version;
      private List<StepDefinition> steps;
      private Map<String, List<String>> dependencies;
      private Map<String, Object> properties;
      private LocalDateTime createdTime;
      private LocalDateTime updatedTime;
      private String threadPoolName;
      private boolean sync;

      private FlowDefinitionBuilder() {
      }

      public FlowDefinitionBuilder id(String id) {
         this.id = id;
         return this;
      }

      public FlowDefinitionBuilder name(String name) {
         this.name = name;
         return this;
      }

      public FlowDefinitionBuilder description(String description) {
         this.description = description;
         return this;
      }

      public FlowDefinitionBuilder version(String version) {
         this.version = version;
         return this;
      }

      public FlowDefinitionBuilder steps(List<StepDefinition> steps) {
         this.steps = steps;
         return this;
      }

      public FlowDefinitionBuilder dependencies(Map<String, List<String>> dependencies) {
         this.dependencies = dependencies;
         return this;
      }

      public FlowDefinitionBuilder properties(Map<String, Object> properties) {
         this.properties = properties;
         return this;
      }

      public FlowDefinitionBuilder createdTime(LocalDateTime createdTime) {
         this.createdTime = createdTime;
         return this;
      }

      public FlowDefinitionBuilder updatedTime(LocalDateTime updatedTime) {
         this.updatedTime = updatedTime;
         return this;
      }

      public FlowDefinitionBuilder threadPoolName(String threadPoolName) {
         this.threadPoolName = threadPoolName;
         return this;
      }

      public FlowDefinitionBuilder sync(boolean sync) {
         this.sync = sync;
         return this;
      }

      public FlowDefinition build() {
         FlowDefinition flowDefinition = new FlowDefinition();
         flowDefinition.setId(this.id);
         flowDefinition.setName(this.name);
         flowDefinition.setDescription(this.description);
         flowDefinition.setVersion(this.version);
         flowDefinition.setSteps(this.steps);
         flowDefinition.setDependencies(this.dependencies);
         flowDefinition.setProperties(this.properties);
         flowDefinition.setCreatedTime(this.createdTime);
         flowDefinition.setUpdatedTime(this.updatedTime);
         flowDefinition.setThreadPoolName(this.threadPoolName);
         flowDefinition.setSync(this.sync);
         return flowDefinition;
      }
   }
}
