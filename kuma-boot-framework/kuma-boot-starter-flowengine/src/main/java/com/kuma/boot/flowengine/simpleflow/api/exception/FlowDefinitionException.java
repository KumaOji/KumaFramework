package com.kuma.boot.flowengine.simpleflow.api.exception;

public class FlowDefinitionException extends FlowException {
   private final String definitionField;
   private final Object invalidValue;

   public FlowDefinitionException(String message) {
      super(message);
      this.definitionField = null;
      this.invalidValue = null;
   }

   public FlowDefinitionException(String message, Throwable cause) {
      super(message, cause);
      this.definitionField = null;
      this.invalidValue = null;
   }

   public FlowDefinitionException(String message, String flowId) {
      super(message, flowId);
      this.definitionField = null;
      this.invalidValue = null;
   }

   public FlowDefinitionException(String message, String flowId, Throwable cause) {
      super(message, flowId, cause);
      this.definitionField = null;
      this.invalidValue = null;
   }

   public FlowDefinitionException(String message, String flowId, String definitionField) {
      super(message, flowId);
      this.definitionField = definitionField;
      this.invalidValue = null;
   }

   public FlowDefinitionException(String message, String flowId, String definitionField, Object invalidValue) {
      super(message, flowId);
      this.definitionField = definitionField;
      this.invalidValue = invalidValue;
   }

   public FlowDefinitionException(String message, String flowId, String definitionField, Object invalidValue, Throwable cause) {
      super(message, flowId, cause);
      this.definitionField = definitionField;
      this.invalidValue = invalidValue;
   }

   public String getDefinitionField() {
      return this.definitionField;
   }

   public Object getInvalidValue() {
      return this.invalidValue;
   }

   public String getDetailedMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append(super.getDetailedMessage());
      if (this.definitionField != null) {
         sb.append(" [Field: ").append(this.definitionField).append("]");
      }

      if (this.invalidValue != null) {
         sb.append(" [InvalidValue: ").append(this.invalidValue).append("]");
      }

      return sb.toString();
   }
}
