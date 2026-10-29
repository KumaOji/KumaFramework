package com.kuma.boot.data.p6spy.logger;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;

public class P6spyLogger extends FormattedLogger {
   public P6spyLogger() {
   }

   public void logException(Exception e) {
      LogUtils.error(e, "数据库日志错误", new Object[0]);
   }

   public void logText(String text) {
   }

   public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
      String msg = this.strategy.formatMessage(connectionId, now, elapsed, category.toString(), prepared, sql, url);
      if (!StringUtils.isEmpty(msg)) {
         if (Category.WARN.equals(category)) {
            LogUtils.warn(msg, new Object[0]);
         } else if (Category.DEBUG.equals(category)) {
            LogUtils.debug(msg, new Object[0]);
         } else {
            LogUtils.info(msg, new Object[0]);
         }

      }
   }

   public boolean isCategoryEnabled(Category category) {
      return true;
   }
}
