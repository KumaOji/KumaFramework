package com.kuma.boot.flowengine.module;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.validator.ValidatorUtils;
import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.exception.FlowException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Flow extends FlowNode {
   private String description;
   private String logName;
   private @Min(1L) @Max(2147483647L) int version;
   private @NotNull @Valid StartNode startNode;
   private @NotNull @Valid EndNode endNode;
   private @NotNull Set<Class<? extends Throwable>> retryException;
   private @Valid List<ActivityNode> nodes = Lists.newArrayList();
   private Map<String, ActivityNode> nodeMap = Maps.newHashMap();
   private @Valid ErrorMonitor errorMonitor;
   private EventListeners eventListeners;
   private @Size(
   min = 1
) List<String> events = Lists.newArrayList();

   public Flow() {
   }

   public void initialize(Flow flow) {
      this.startNode.initialize(this);
   }

   public void execute(Execution execution) {
      this.startNode.execute(execution);
   }

   public void validate() {
      Set<ConstraintViolation<AbstractNode>> constraintViolations = ValidatorUtils.validate(this, new Class[0]);
      if (constraintViolations != null && !constraintViolations.isEmpty()) {
         StringBuilder errorMessage = new StringBuilder();
         errorMessage.append("Flow\u5b9a\u4e49\u68c0\u67e5\u51fa\u9519,name=").append(this.getName()).append(",version=").append(this.version).append("");

         for(ConstraintViolation<AbstractNode> constraintViolation : constraintViolations) {
            errorMessage.append("\u9519\u8bef\u4fe1\u606f:").append(constraintViolation.getPropertyPath().toString()).append("->").append(constraintViolation.getMessage()).append(",\u8282\u70b9\u4fe1\u606f:").append(constraintViolation.getLeafBean().toString()).append(",");
         }

         throw new FlowException(errorMessage.substring(0, errorMessage.length() - 1));
      }
   }

   public Set<Class<? extends Throwable>> getRetryException() {
      return this.retryException;
   }

   public void setRetryException(Set<Class<? extends Throwable>> retryException) {
      this.retryException = retryException;
   }

   public ActivityNode nodeByName(String name) {
      return (ActivityNode)this.nodeMap.get(name);
   }

   public ActivityNode lastAddedNode() {
      return (ActivityNode)this.getNodes().get(this.getNodes().size() - 1);
   }

   public boolean notExistNode(String node) {
      return null == node || null == this.nodeByName(node) && this.notStartAndEndNode(node);
   }

   private boolean notStartAndEndNode(String node) {
      return null == node || !this.startNode.getName().equals(node) && !this.endNode.getName().equals(node);
   }

   public List<String> getEvents() {
      return this.events;
   }

   public EndNode getEndNode() {
      return this.endNode;
   }

   public void setEndNode(EndNode endNode) {
      this.endNode = endNode;
   }

   public StartNode getStartNode() {
      return this.startNode;
   }

   public void setStartNode(StartNode startNode) {
      this.startNode = startNode;
   }

   public void addNode(ActivityNode node) {
      this.nodes.add(node);
      this.nodeMap.put(node.getName(), node);
   }

   public List<ActivityNode> getNodes() {
      return this.nodes;
   }

   public void setVersion(int version) {
      this.version = version;
   }

   public int getVersion() {
      return this.version;
   }

   public ErrorMonitor getErrorMonitor() {
      return this.errorMonitor;
   }

   public void setErrorMonitor(ErrorMonitor errorMonitor) {
      this.errorMonitor = errorMonitor;
   }

   public EventListeners getEventListeners() {
      return this.eventListeners;
   }

   public void setEventListeners(EventListeners eventListeners) {
      this.eventListeners = eventListeners;
   }

   public void addEvent(String event) {
      this.events.add(event);
   }

   public String getLogName() {
      return this.logName;
   }

   public void setLogName(String logName) {
      this.logName = logName;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("Flow{");
      sb.append("getName=").append(this.getName());
      sb.append("version=").append(this.version);
      sb.append("description=").append(this.description);
      sb.append("getTriggerClass=").append(this.getTriggerClass());
      sb.append("endNode=").append(this.endNode);
      sb.append("errorMonitor=").append(this.errorMonitor);
      sb.append("nodes=").append(this.nodes);
      sb.append("startNode=").append(this.startNode);
      sb.append("logName=").append(this.logName);
      sb.append('}');
      return sb.toString();
   }

   public static class Key {
      private String flowName;
      private int version;

      public Key(String flowName, int version) {
         this.flowName = flowName;
         this.version = version;
      }

      public String getFlowName() {
         return this.flowName;
      }

      public void setFlowName(String flowName) {
         this.flowName = flowName;
      }

      public int getVersion() {
         return this.version;
      }

      public void setVersion(int version) {
         this.version = version;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Key key = (Key)o;
            return this.version == key.version && Objects.equals(this.flowName, key.flowName);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.flowName, this.version});
      }

      public String toString() {
         return "Key{flowName='" + this.flowName + "', version=" + this.version + "}";
      }
   }
}
