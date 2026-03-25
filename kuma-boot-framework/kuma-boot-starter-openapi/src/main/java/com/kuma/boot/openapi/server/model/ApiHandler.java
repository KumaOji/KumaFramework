package com.kuma.boot.openapi.server.model;

import com.kuma.boot.openapi.server.annotation.OpenApiMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ApiHandler {
   private String openApiName;
   private String openApiMethodName;
   private String beanName;
   private Object bean;
   private Method method;
   private Type[] paramTypes;
   private Parameter[] parameters;
   private OpenApiMethod openApiMethod;

   public String toString() {
      return String.format("%s:%s:%s", this.bean.getClass().getSimpleName(), this.method.getName(), Arrays.asList(this.paramTypes));
   }

   public String getOpenApiName() {
      return this.openApiName;
   }

   public void setOpenApiName(String openApiName) {
      this.openApiName = openApiName;
   }

   public String getOpenApiMethodName() {
      return this.openApiMethodName;
   }

   public void setOpenApiMethodName(String openApiMethodName) {
      this.openApiMethodName = openApiMethodName;
   }

   public String getBeanName() {
      return this.beanName;
   }

   public void setBeanName(String beanName) {
      this.beanName = beanName;
   }

   public Object getBean() {
      return this.bean;
   }

   public void setBean(Object bean) {
      this.bean = bean;
   }

   public Method getMethod() {
      return this.method;
   }

   public void setMethod(Method method) {
      this.method = method;
   }

   public Type[] getParamTypes() {
      return this.paramTypes;
   }

   public void setParamTypes(Type[] paramTypes) {
      this.paramTypes = paramTypes;
   }

   public Parameter[] getParameters() {
      return this.parameters;
   }

   public void setParameters(Parameter[] parameters) {
      this.parameters = parameters;
   }

   public OpenApiMethod getOpenApiMethod() {
      return this.openApiMethod;
   }

   public void setOpenApiMethod(OpenApiMethod openApiMethod) {
      this.openApiMethod = openApiMethod;
   }
}
