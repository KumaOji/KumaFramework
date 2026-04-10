package com.kuma.boot.job.xxl.executor.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.job.xxl.autoconfigure.properties.XxlJobProperties;
import com.kuma.boot.job.xxl.executor.model.XxlJobGroup;
import com.kuma.boot.job.xxl.executor.service.JobGroupService;
import com.kuma.boot.job.xxl.executor.service.JobLoginService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class JobGroupServiceImpl implements JobGroupService {
   private final XxlJobProperties xxlJobProperties;
   private final JobLoginService jobLoginService;

   public JobGroupServiceImpl(XxlJobProperties xxlJobProperties, JobLoginService jobLoginService) {
      this.xxlJobProperties = xxlJobProperties;
      this.jobLoginService = jobLoginService;
   }

   public List<XxlJobGroup> getJobGroup() {
      String url = this.xxlJobProperties.getAdmin().getAddresses() + "/jobgroup/pageList";
      Map<String, Object> formMap = new HashMap();
      formMap.put("appname", this.xxlJobProperties.getExecutor().getAppname());
      formMap.put("title", this.xxlJobProperties.getExecutor().getTitle());
      String body = HttpUtil.createPost(url).form(formMap).cookie(this.jobLoginService.getCookie()).execute().body();
      JSONArray array = (JSONArray)JSONUtil.parse(body).getByPath("data", JSONArray.class);
      return JSONUtil.toList(array, XxlJobGroup.class);
   }

   public boolean autoRegisterGroup() {
      String url = this.xxlJobProperties.getAdmin().getAddresses() + "/jobgroup/save";
      Map<String, Object> formMap = new HashMap();
      formMap.put("appname", this.xxlJobProperties.getExecutor().getAppname());
      formMap.put("title", this.xxlJobProperties.getExecutor().getTitle());
      String body = HttpUtil.createPost(url).form(formMap).cookie(this.jobLoginService.getCookie()).execute().body();
      Integer code = (Integer)JSONUtil.parse(body).getByPath("code", Integer.class);
      return code.equals(200);
   }

   public boolean preciselyCheck() {
      List<XxlJobGroup> jobGroup = this.getJobGroup();
      Optional<XxlJobGroup> has = jobGroup.stream().filter((xxlJobGroup) -> xxlJobGroup.getAppname().equals(this.xxlJobProperties.getExecutor().getAppname()) && xxlJobGroup.getTitle().equals(this.xxlJobProperties.getExecutor().getTitle())).findAny();
      return has.isPresent();
   }
}
