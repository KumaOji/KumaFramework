package com.kuma.boot.websocket.stomp.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kuma.boot.websocket.stomp.core.PrincipalDetails;
import java.security.Principal;
import java.util.Set;

public class WebSocketPrincipal implements Principal {
   private String userId;
   private String userName;
   private String employeeId;
   private String avatar;
   private Set<String> roles;

   public WebSocketPrincipal(PrincipalDetails details) {
      this.userId = details.getOpenId();
      this.userName = details.getUserName();
      this.employeeId = details.getEmployeeId();
      this.avatar = details.getAvatar();
      this.roles = details.getRoles();
   }

   public WebSocketPrincipal(String userId) {
      this.userId = userId;
   }

   public String getUserName() {
      return this.userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getName() {
      return this.userId;
   }

   public String getUserId() {
      return this.userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getEmployeeId() {
      return this.employeeId;
   }

   public void setEmployeeId(String employeeId) {
      this.employeeId = employeeId;
   }

   public String getAvatar() {
      return this.avatar;
   }

   public void setAvatar(String avatar) {
      this.avatar = avatar;
   }

   public Set<String> getRoles() {
      return this.roles;
   }

   public void setRoles(Set<String> roles) {
      this.roles = roles;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         WebSocketPrincipal that = (WebSocketPrincipal)o;
         return Objects.equal(this.userId, that.userId);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.userId});
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("userId", this.userId).add("userName", this.userName).add("employeeId", this.employeeId).add("avatar", this.avatar).toString();
   }
}
