package com.kuma.boot.job.xxl.executor.service;

import com.kuma.boot.job.xxl.executor.model.XxlJobInfo;
import java.util.List;

public interface JobInfoService {
   List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

   Integer addJobInfo(XxlJobInfo xxlJobInfo);
}
