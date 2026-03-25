package com.kuma.boot.encrypt.encrypt1.commonresponse;

public class RespBean {
   private Integer status;
   private String msg;
   private Object obj;

   public static RespBean build() {
      return new RespBean();
   }

   public static RespBean ok(String msg) {
      return new RespBean(200, msg, (Object)null);
   }

   public static RespBean ok(String msg, Object obj) {
      return new RespBean(200, msg, obj);
   }

   public static RespBean error(String msg) {
      return new RespBean(500, msg, (Object)null);
   }

   public static RespBean error(String msg, Object obj) {
      return new RespBean(500, msg, obj);
   }

   private RespBean() {
   }

   private RespBean(Integer status, String msg, Object obj) {
      this.status = status;
      this.msg = msg;
      this.obj = obj;
   }

   public Integer getStatus() {
      return this.status;
   }

   public RespBean setStatus(Integer status) {
      this.status = status;
      return this;
   }

   public String getMsg() {
      return this.msg;
   }

   public RespBean setMsg(String msg) {
      this.msg = msg;
      return this;
   }

   public Object getObj() {
      return this.obj;
   }

   public RespBean setObj(Object obj) {
      this.obj = obj;
      return this;
   }
}
