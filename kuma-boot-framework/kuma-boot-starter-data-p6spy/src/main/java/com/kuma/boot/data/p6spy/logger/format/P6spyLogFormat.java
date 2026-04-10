package com.kuma.boot.data.p6spy.logger.format;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.SqlFormatter;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class P6spyLogFormat implements MessageFormattingStrategy {
   public P6spyLogFormat() {
   }

   public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url) {
      return "    Consume Time: %s  Now: %s connectionId: %s category: %s\n    Execute SQL\uff1a%s\n".formatted(elapsed, now, connectionId, category, StrUtil.isBlank(sql) ? "" : SqlFormatter.format(sql.replaceAll("[\\s]+", " ")));
   }
}
