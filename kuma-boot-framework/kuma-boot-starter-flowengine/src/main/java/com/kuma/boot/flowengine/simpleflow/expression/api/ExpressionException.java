package com.kuma.boot.flowengine.simpleflow.expression.api;

public class ExpressionException extends Exception {
   private final String expression;
   private final int position;
   private final String errorCode;

   public ExpressionException(String message) {
      super(message);
      this.expression = null;
      this.position = -1;
      this.errorCode = null;
   }

   public ExpressionException(String message, Throwable cause) {
      super(message, cause);
      this.expression = null;
      this.position = -1;
      this.errorCode = null;
   }

   public ExpressionException(String message, String expression) {
      super(message);
      this.expression = expression;
      this.position = -1;
      this.errorCode = null;
   }

   public ExpressionException(String message, String expression, Throwable cause) {
      super(message, cause);
      this.expression = expression;
      this.position = -1;
      this.errorCode = null;
   }

   public ExpressionException(String message, String expression, int position) {
      super(message);
      this.expression = expression;
      this.position = position;
      this.errorCode = null;
   }

   public ExpressionException(String message, String expression, int position, Throwable cause) {
      super(message, cause);
      this.expression = expression;
      this.position = position;
      this.errorCode = null;
   }

   public ExpressionException(String message, String expression, int position, String errorCode) {
      super(message);
      this.expression = expression;
      this.position = position;
      this.errorCode = errorCode;
   }

   public ExpressionException(String message, String expression, int position, String errorCode, Throwable cause) {
      super(message, cause);
      this.expression = expression;
      this.position = position;
      this.errorCode = errorCode;
   }

   public String getExpression() {
      return this.expression;
   }

   public int getPosition() {
      return this.position;
   }

   public String getErrorCode() {
      return this.errorCode;
   }

   public String getDetailedMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getMessage());
      if (this.expression != null) {
         sb.append(" [Expression: ").append(this.expression).append("]");
      }

      if (this.position >= 0) {
         sb.append(" [Position: ").append(this.position).append("]");
      }

      if (this.errorCode != null) {
         sb.append(" [ErrorCode: ").append(this.errorCode).append("]");
      }

      return sb.toString();
   }

   public String getExpressionWithPosition() {
      if (this.expression != null && this.position >= 0) {
         StringBuilder sb = new StringBuilder();
         sb.append(this.expression).append("\n");

         for(int i = 0; i < this.position; ++i) {
            sb.append(" ");
         }

         sb.append("^");
         return sb.toString();
      } else {
         return this.expression;
      }
   }

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + ": " + this.getDetailedMessage();
   }
}
