package com.kuma.boot.job.xxl.aspect;

public interface XxlJobExceptionHandler {
   void handleException(String jobName, long jobId, String param, long timestamp, Exception e);
}
