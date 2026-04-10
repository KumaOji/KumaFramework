package com.kuma.boot.job.xxl.executor.service;

import com.kuma.boot.job.xxl.executor.model.XxlJobGroup;
import java.util.List;

public interface JobGroupService {
   List<XxlJobGroup> getJobGroup();

   boolean autoRegisterGroup();

   boolean preciselyCheck();
}
