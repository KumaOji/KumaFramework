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
      LogUtils.info("\u8bf7\u6c42\u53c2\u6570: {}", new Object[]{param});
      XxlJobHelper.log("\u8bf7\u6c42\u53c2\u6570: {}", new Object[]{param});
      T cmd = null;
      if (StrUtil.isNotEmpty(param)) {
         try {
            cmd = (T)JSON.parseObject(param, clazz);
         } catch (Exception e) {
            LogUtils.error("\u8bf7\u6c42\u53c2\u6570\u8f6c\u6362\u5f02\u5e38: {}", new Object[]{e.getMessage(), e});
            XxlJobHelper.log("\u8bf7\u6c42\u53c2\u6570: {}", new Object[]{e.getMessage()});
         }
      }

      if (cmd == null) {
         cmd = (T)JSON.parseObject("{}", clazz);
      }

      XxlJobContext xxlJobContext = XxlJobContext.getXxlJobContext();
      return cmd;
   }
}
