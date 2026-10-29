package com.kuma.boot.job.xxl.base;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.log.LogUtils;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;

public class BaseSchedule {
   public BaseSchedule() {
   }

   protected <T> T from(String param, Class<T> clazz) {
      LogUtils.info("请求参数: {}", new Object[]{param});
      XxlJobHelper.log("请求参数: {}", new Object[]{param});
      T cmd = null;
      if (StrUtil.isNotEmpty(param)) {
         try {
            cmd = (T)JSON.parseObject(param, clazz);
         } catch (Exception e) {
            LogUtils.error("请求参数转换异常: {}", new Object[]{e.getMessage(), e});
            XxlJobHelper.log("请求参数: {}", new Object[]{e.getMessage()});
         }
      }

      if (cmd == null) {
         cmd = (T)JSON.parseObject("{}", clazz);
      }

      XxlJobContext xxlJobContext = XxlJobContext.getXxlJobContext();
      return cmd;
   }
}
