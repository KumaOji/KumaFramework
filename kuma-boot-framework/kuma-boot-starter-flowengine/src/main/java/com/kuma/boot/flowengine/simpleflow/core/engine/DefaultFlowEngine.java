package com.kuma.boot.flowengine.simpleflow.core.engine;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.FlowEngine;
import com.kuma.boot.flowengine.simpleflow.api.exception.FlowDefinitionException;
import com.kuma.boot.flowengine.simpleflow.api.exception.FlowException;
import com.kuma.boot.flowengine.simpleflow.api.exception.FlowExecutionException;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowDefinition;
import com.kuma.boot.flowengine.simpleflow.api.model.FlowResult;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultFlowEngine implements FlowEngine {
   private final Map<String, FlowDefinition> flowDefinitions = new ConcurrentHashMap();
   private final Map<String, FlowExecution> activeExecutions = new ConcurrentHashMap();
   private final ExecutorService executorService;
   private final AtomicLong executionIdGenerator = new AtomicLong(0L);
   private final AtomicBoolean shutdown = new AtomicBoolean(false);

   public DefaultFlowEngine() {
      this.executorService = Executors.newCachedThreadPool((r) -> {
         Thread thread = new Thread(r, "SimpleFlow-Worker-" + Thread.currentThread().getId());
         thread.setDaemon(true);
         return thread;
      });
   }

   public DefaultFlowEngine(ExecutorService executorService) {
      this.executorService = executorService;
   }

   public FlowResult execute(FlowDefinition flowDefinition, Map<String, Object> input) {
      this.checkShutdown();
      String executionId = this.generateExecutionId();
      LogUtils.info("Starting synchronous execution of flow '{}' with execution ID '{}'", new Object[]{flowDefinition.getId(), executionId});

      FlowResult var6;
      try {
         FlowExecution execution = new FlowExecution(executionId, flowDefinition, input);
         this.activeExecutions.put(executionId, execution);
         FlowResult result = execution.execute();
         LogUtils.info("Completed synchronous execution of flow '{}' with execution ID '{}', status: {}", new Object[]{flowDefinition.getId(), executionId, result.getStatus()});
         var6 = result;
      } catch (Exception e) {
         LogUtils.error("Failed to execute flow '{}' with execution ID '{}'", new Object[]{flowDefinition.getId(), executionId, e});
         throw new FlowExecutionException("Flow execution failed: " + e.getMessage(), flowDefinition.getId(), executionId, e);
      } finally {
         this.activeExecutions.remove(executionId);
      }

      return var6;
   }

   public FlowResult execute(String flowId, Map<String, Object> input) throws FlowException {
      this.checkShutdown();
      FlowDefinition flowDefinition = this.getFlowDefinition(flowId);
      if (flowDefinition == null) {
         throw new FlowDefinitionException("Flow definition not found: " + flowId, flowId);
      } else {
         return this.execute(flowDefinition, input);
      }
   }

   public CompletableFuture<FlowResult> executeAsync(FlowDefinition flowDefinition, Map<String, Object> input) {
      this.checkShutdown();
      String executionId = this.generateExecutionId();
      LogUtils.info("Starting asynchronous execution of flow '{}' with execution ID '{}'", new Object[]{flowDefinition.getId(), executionId});
      return CompletableFuture.supplyAsync(() -> {
         FlowResult var6;
         try {
            FlowExecution execution = new FlowExecution(executionId, flowDefinition, input);
            this.activeExecutions.put(executionId, execution);
            FlowResult result = execution.execute();
            LogUtils.info("Completed asynchronous execution of flow '{}' with execution ID '{}', status: {}", new Object[]{flowDefinition.getId(), executionId, result.getStatus()});
            var6 = result;
         } catch (Exception e) {
            LogUtils.error("Failed to execute flow '{}' with execution ID '{}'", new Object[]{flowDefinition.getId(), executionId, e});
            throw new FlowExecutionException("Flow execution failed: " + e.getMessage(), flowDefinition.getId(), executionId, e);
         } finally {
            this.activeExecutions.remove(executionId);
         }

         return var6;
      }, this.executorService);
   }

   public CompletableFuture<FlowResult> executeAsync(String flowId, Map<String, Object> input) throws FlowException {
      this.checkShutdown();
      FlowDefinition flowDefinition = this.getFlowDefinition(flowId);
      if (flowDefinition == null) {
         throw new FlowDefinitionException("Flow definition not found: " + flowId, flowId);
      } else {
         return this.executeAsync(flowDefinition, input);
      }
   }

   public boolean stopExecution(String executionId) throws FlowException {
      this.checkShutdown();
      FlowExecution execution = (FlowExecution)this.activeExecutions.get(executionId);
      if (execution == null) {
         LogUtils.warn("Execution not found: {}", new Object[]{executionId});
         return false;
      } else {
         LogUtils.info("Stopping execution: {}", new Object[]{executionId});
         boolean stopped = execution.stop();
         if (stopped) {
            this.activeExecutions.remove(executionId);
            LogUtils.info("Successfully stopped execution: {}", new Object[]{executionId});
         } else {
            LogUtils.warn("Failed to stop execution: {}", new Object[]{executionId});
         }

         return stopped;
      }
   }

   public boolean pauseExecution(String executionId) throws FlowException {
      this.checkShutdown();
      FlowExecution execution = (FlowExecution)this.activeExecutions.get(executionId);
      if (execution == null) {
         LogUtils.warn("Execution not found: {}", new Object[]{executionId});
         return false;
      } else {
         LogUtils.info("Pausing execution: {}", new Object[]{executionId});
         return execution.pause();
      }
   }

   public boolean resumeExecution(String executionId) throws FlowException {
      this.checkShutdown();
      FlowExecution execution = (FlowExecution)this.activeExecutions.get(executionId);
      if (execution == null) {
         LogUtils.warn("Execution not found: {}", new Object[]{executionId});
         return false;
      } else {
         LogUtils.info("Resuming execution: {}", new Object[]{executionId});
         return execution.resume();
      }
   }

   public String getExecutionStatus(String executionId) throws FlowException {
      FlowExecution execution = (FlowExecution)this.activeExecutions.get(executionId);
      return execution == null ? "NOT_FOUND" : execution.getStatus();
   }

   public String registerFlow(FlowDefinition flowDefinition) throws FlowException {
      this.checkShutdown();
      if (flowDefinition == null) {
         throw new FlowDefinitionException("Flow definition cannot be null");
      } else if (flowDefinition.getId() != null && !flowDefinition.getId().trim().isEmpty()) {
         this.validateFlowDefinition(flowDefinition);
         this.flowDefinitions.put(flowDefinition.getId(), flowDefinition);
         LogUtils.info("Registered flow definition: {} (version: {})", new Object[]{flowDefinition.getId(), flowDefinition.getVersion()});
         return flowDefinition.getId();
      } else {
         throw new FlowDefinitionException("Flow ID cannot be null or empty");
      }
   }

   public boolean unregisterFlow(String flowId) throws FlowException {
      this.checkShutdown();
      if (flowId != null && !flowId.trim().isEmpty()) {
         FlowDefinition removed = (FlowDefinition)this.flowDefinitions.remove(flowId);
         if (removed != null) {
            LogUtils.info("Unregistered flow definition: {}", new Object[]{flowId});
            return true;
         } else {
            LogUtils.warn("Flow definition not found for unregistration: {}", new Object[]{flowId});
            return false;
         }
      } else {
         throw new FlowDefinitionException("Flow ID cannot be null or empty");
      }
   }

   public FlowDefinition getFlowDefinition(String flowId) throws FlowException {
      if (flowId != null && !flowId.trim().isEmpty()) {
         return (FlowDefinition)this.flowDefinitions.get(flowId);
      } else {
         throw new FlowDefinitionException("Flow ID cannot be null or empty");
      }
   }

   public boolean isHealthy() {
      return !this.shutdown.get() && !this.executorService.isShutdown();
   }

   public void shutdown() {
      if (this.shutdown.compareAndSet(false, true)) {
         LogUtils.info("Shutting down flow engine...", new Object[0]);
         this.activeExecutions.values().forEach((execution) -> {
            try {
               execution.stop();
            } catch (Exception e) {
               LogUtils.warn("Error stopping execution during shutdown: {}", new Object[]{execution.getExecutionId(), e});
            }

         });
         this.executorService.shutdown();
         LogUtils.info("Flow engine shutdown completed", new Object[0]);
      }

   }

   private void checkShutdown() {
      if (this.shutdown.get()) {
         throw new FlowException("Flow engine has been shutdown");
      }
   }

   private String generateExecutionId() {
      long var10000 = System.currentTimeMillis();
      return "exec-" + var10000 + "-" + this.executionIdGenerator.incrementAndGet();
   }

   private void validateFlowDefinition(FlowDefinition flowDefinition) throws FlowDefinitionException {
      if (flowDefinition.hasCyclicDependency()) {
         throw new FlowDefinitionException("Flow definition contains cyclic dependencies", flowDefinition.getId());
      } else if (flowDefinition.getSteps() == null || flowDefinition.getSteps().isEmpty()) {
         throw new FlowDefinitionException("Flow definition must contain at least one step", flowDefinition.getId());
      }
   }

   public int getActiveExecutionCount() {
      return this.activeExecutions.size();
   }

   public int getRegisteredFlowCount() {
      return this.flowDefinitions.size();
   }
}
