package com.kuma.boot.oss.common.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.oss.common.constant.OssConstant;

public class SliceConfig {
   private Long partSize;
   private Integer taskNum;

   public void init() {
      if (this.getPartSize() <= 0L) {
         LogUtils.error("断点续传——分片大小必须大于0", new Object[0]);
         this.setPartSize(OssConstant.DEFAULT_PART_SIZE);
      }

      if (this.getTaskNum() <= 0) {
         LogUtils.error("断点续传——并发线程数必须大于0", new Object[0]);
         this.setTaskNum(OssConstant.DEFAULT_TASK_NUM);
      }

   }

   public SliceConfig() {
      this.partSize = OssConstant.DEFAULT_PART_SIZE;
      this.taskNum = OssConstant.DEFAULT_TASK_NUM;
   }

   public SliceConfig(Long partSize, Integer taskNum) {
      this.partSize = OssConstant.DEFAULT_PART_SIZE;
      this.taskNum = OssConstant.DEFAULT_TASK_NUM;
      this.partSize = partSize;
      this.taskNum = taskNum;
   }

   public Long getPartSize() {
      return this.partSize;
   }

   public void setPartSize(Long partSize) {
      this.partSize = partSize;
   }

   public Integer getTaskNum() {
      return this.taskNum;
   }

   public void setTaskNum(Integer taskNum) {
      this.taskNum = taskNum;
   }
}
