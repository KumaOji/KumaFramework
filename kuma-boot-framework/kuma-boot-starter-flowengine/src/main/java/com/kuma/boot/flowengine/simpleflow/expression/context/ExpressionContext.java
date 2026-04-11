package com.kuma.boot.flowengine.simpleflow.expression.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ExpressionContext {
   private final Map<String, Object> variables;
   private final Map<String, Object> functions;
   private final ExpressionContext parent;

   public ExpressionContext() {
      this.variables = new HashMap();
      this.functions = new HashMap();
      this.parent = null;
   }

   public ExpressionContext(ExpressionContext parent) {
      this.variables = new HashMap();
      this.functions = new HashMap();
      this.parent = parent;
   }

   public ExpressionContext(Map<String, Object> initialVariables) {
      this.parent = null;
      this.variables = new HashMap();
      this.functions = new HashMap();
      if (initialVariables != null) {
         this.variables.putAll(initialVariables);
      }

   }

   public ExpressionContext(ExpressionContext parent, Map<String, Object> initialVariables) {
      this.parent = parent;
      this.variables = new HashMap();
      this.functions = new HashMap();
      if (initialVariables != null) {
         this.variables.putAll(initialVariables);
      }

   }

   public ExpressionContext setVariable(String name, Object value) {
      Objects.requireNonNull(name, "Variable name cannot be null");
      this.variables.put(name, value);
      return this;
   }

   public ExpressionContext setVariables(Map<String, Object> variables) {
      if (variables != null) {
         this.variables.putAll(variables);
      }

      return this;
   }

   public Object getVariable(String name) {
      Objects.requireNonNull(name, "Variable name cannot be null");
      if (this.variables.containsKey(name)) {
         return this.variables.get(name);
      } else {
         return this.parent != null ? this.parent.getVariable(name) : null;
      }
   }

   public <T> T getVariable(String name, Class<T> type) {
      Object value = this.getVariable(name);
      return (T)(value != null && type.isInstance(value) ? value : null);
   }

   public boolean hasVariable(String name) {
      Objects.requireNonNull(name, "Variable name cannot be null");
      if (this.variables.containsKey(name)) {
         return true;
      } else {
         return this.parent != null && this.parent.hasVariable(name);
      }
   }

   public Object removeVariable(String name) {
      Objects.requireNonNull(name, "Variable name cannot be null");
      return this.variables.remove(name);
   }

   public Set<String> getVariableNames() {
      Set<String> names = (new HashMap(this.variables)).keySet();
      if (this.parent != null) {
         names.addAll(this.parent.getVariableNames());
      }

      return Collections.unmodifiableSet(names);
   }

   public Map<String, Object> getVariables() {
      return Collections.unmodifiableMap(this.variables);
   }

   public Map<String, Object> getAllVariables() {
      Map<String, Object> allVariables = new HashMap();
      if (this.parent != null) {
         allVariables.putAll(this.parent.getAllVariables());
      }

      allVariables.putAll(this.variables);
      return allVariables;
   }

   public ExpressionContext setFunction(String name, Object function) {
      Objects.requireNonNull(name, "Function name cannot be null");
      Objects.requireNonNull(function, "Function cannot be null");
      this.functions.put(name, function);
      return this;
   }

   public Object getFunction(String name) {
      Objects.requireNonNull(name, "Function name cannot be null");
      if (this.functions.containsKey(name)) {
         return this.functions.get(name);
      } else {
         return this.parent != null ? this.parent.getFunction(name) : null;
      }
   }

   public boolean hasFunction(String name) {
      Objects.requireNonNull(name, "Function name cannot be null");
      if (this.functions.containsKey(name)) {
         return true;
      } else {
         return this.parent != null && this.parent.hasFunction(name);
      }
   }

   public Object removeFunction(String name) {
      Objects.requireNonNull(name, "Function name cannot be null");
      return this.functions.remove(name);
   }

   public Set<String> getFunctionNames() {
      Set<String> names = (new HashMap(this.functions)).keySet();
      if (this.parent != null) {
         names.addAll(this.parent.getFunctionNames());
      }

      return Collections.unmodifiableSet(names);
   }

   public Map<String, Object> getFunctions() {
      return Collections.unmodifiableMap(this.functions);
   }

   public ExpressionContext createChild() {
      return new ExpressionContext(this);
   }

   public ExpressionContext createChild(Map<String, Object> initialVariables) {
      return new ExpressionContext(this, initialVariables);
   }

   public ExpressionContext getParent() {
      return this.parent;
   }

   public void clear() {
      this.variables.clear();
      this.functions.clear();
   }

   public boolean isEmpty() {
      return this.variables.isEmpty() && this.functions.isEmpty() && (this.parent == null || this.parent.isEmpty());
   }

   public int size() {
      int size = this.variables.size() + this.functions.size();
      if (this.parent != null) {
         size += this.parent.size();
      }

      return size;
   }

   public String toString() {
      String var10000 = String.valueOf(this.variables.keySet());
      return "ExpressionContext{variables=" + var10000 + ", functions=" + String.valueOf(this.functions.keySet()) + ", hasParent=" + (this.parent != null) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ExpressionContext that = (ExpressionContext)o;
         return Objects.equals(this.variables, that.variables) && Objects.equals(this.functions, that.functions) && Objects.equals(this.parent, that.parent);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.variables, this.functions, this.parent});
   }
}
