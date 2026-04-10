package com.kuma.boot.job.xxl.executor.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.xxl.autoconfigure.properties.XxlJobProperties;
import com.kuma.boot.job.xxl.executor.service.JobLoginService;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class JobLoginServiceImpl implements JobLoginService {
   private final XxlJobProperties xxlJobProperties;
   private final Map<String, String> loginCookie = new HashMap();

   public JobLoginServiceImpl(XxlJobProperties xxlJobProperties) {
      this.xxlJobProperties = xxlJobProperties;
   }

   public void login() {
      if (StrUtil.isBlank(this.xxlJobProperties.getAdmin().getAddresses())) {
         throw new RuntimeException("xxl admin address url \u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         String url = this.xxlJobProperties.getAdmin().getAddresses() + "/auth/doLogin";
         Map<String, Object> formMap = new HashMap();
         formMap.put("userName", this.xxlJobProperties.getAdmin().getUsername());
         formMap.put("password", this.xxlJobProperties.getAdmin().getPassword());
         HttpResponse send = HttpUtil.createPost(url).form(formMap).execute();
         String cookieStr = send.getCookieStr();
         List<HttpCookie> parse = HttpCookie.parse(cookieStr);
         String value = ((HttpCookie)parse.getFirst()).getValue();
         this.loginCookie.put("xxl_job_login_token", value);
      }
   }

   public String getCookie() {
      for(int i = 0; i < 3; ++i) {
         String cookieStr = (String)this.loginCookie.get("xxl_job_login_token");
         if (cookieStr != null) {
            return "xxl_job_login_token=" + cookieStr;
         }

         try {
            this.login();
         } catch (Exception var4) {
            LogUtils.error("\u83b7\u53d6xxljob cookieStr \u9519\u8bef, \u6b21\u6570: {}", new Object[]{i});
         }
      }

      throw new RuntimeException("get xxl-job cookie error!");
   }
}
