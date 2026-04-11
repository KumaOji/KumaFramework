package com.kuma.boot.websocket.original;

public class SendMsgAll {
   private String msg;
   private String type;
   private String projectId;

   public SendMsgAll() {
   }

   public String getMsg() {
      return this.msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getProjectId() {
      return this.projectId;
   }

   public void setProjectId(String projectId) {
      this.projectId = projectId;
   }
}
