package com.kuma.boot.job.xxl.aspect;

/** <b>XxlJob 异常处理器接口</b> */
public interface XxlJobExceptionHandler
{
   void handleException(String jobName, long jobId, String param, long timestamp, Exception e);
}
