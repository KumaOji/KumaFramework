package com.kuma.boot.job.xxl.executor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.job.xxl.autoconfigure.properties.XxlJobProperties;
import com.kuma.boot.job.xxl.executor.model.XxlJobInfo;
import com.kuma.boot.job.xxl.executor.service.JobInfoService;
import com.kuma.boot.job.xxl.executor.service.JobLoginService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class JobInfoServiceImpl implements JobInfoService {
   private final XxlJobProperties xxlJobProperties;
   private final JobLoginService jobLoginService;

   public JobInfoServiceImpl(XxlJobProperties xxlJobProperties, JobLoginService jobLoginService) {
      this.xxlJobProperties = xxlJobProperties;
      this.jobLoginService = jobLoginService;
   }

   public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
      String url = this.xxlJobProperties.getAdmin().getAddresses() + "/jobinfo/pageList";
      Map<String, Object> formMap = new HashMap();
      formMap.put("jobGroup", jobGroupId);
      formMap.put("executorHandler", executorHandler);
      formMap.put("triggerStatus", -1);
      formMap.put("jobDesc", (Object)null);
      formMap.put("author", (Object)null);
      formMap.put("start", 0);
      formMap.put("length", 100);
      String body = HttpUtil.createPost(url).form(formMap).cookie(this.jobLoginService.getCookie()).execute().body();
      JSONArray array = (JSONArray)JSONUtil.parse(body).getByPath("data", JSONArray.class);
      return JSONUtil.toList(array, XxlJobInfo.class);
   }

   public Integer addJobInfo(XxlJobInfo xxlJobInfo) {
      String url = this.xxlJobProperties.getAdmin().getAddresses() + "/jobinfo/add";
      Map<String, Object> formMap = BeanUtil.beanToMap(xxlJobInfo, new String[0]);
      String body = HttpUtil.createPost(url).form(formMap).cookie(this.jobLoginService.getCookie()).execute().body();
      JSON json = JSONUtil.parse(body);
      Integer code = (Integer)json.getByPath("code", Integer.class);
      if (code.equals(200)) {
         return (Integer)json.getByPath("content", Integer.class);
      } else {
         throw new RuntimeException("add jobInfo error!");
      }
   }
}
