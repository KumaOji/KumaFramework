package com.taotao.boot.client.forest.auth;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.secure.Base64Utils;

public class MyAuthLifeCycle implements MethodAnnotationLifeCycle<MyAuth, Object> {
   public MyAuthLifeCycle() {
   }

   public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
      LogUtils.info("Invoke Method '" + method.getMethodName() + "' Arguments: " + String.valueOf(args), new Object[0]);
   }

   public boolean beforeExecute(ForestRequest request) {
      String username = (String)this.getAttribute(request, "username");
      String password = (String)this.getAttribute(request, "password");
      String var10000 = "{" + username + ":" + password + "}";
      String basic = "MyAuth " + Base64Utils.encode(var10000);
      request.addHeader("MyAuthorization", basic);
      return true;
   }

   public void onMethodInitialized(ForestMethod method, MyAuth annotation) {
      LogUtils.info("Method '" + method.getMethodName() + "' Initialized, Arguments: " + String.valueOf(annotation), new Object[0]);
   }
}
