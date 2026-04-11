package com.taotao.boot.client.forest.config;

import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.logging.ResponseLogMessage;
import com.dtflys.forest.utils.StringUtils;

public class MyLogHandler extends DefaultLogHandler {
   public MyLogHandler() {
   }

   public void logContent(String content) {
      super.logContent("[\u54c8\u54c8\uff0c\u8fd9\u662f\u6211\u81ea\u5df1\u7684\u65e5\u5fd7]: " + content);
   }

   protected String requestLoggingContent(RequestLogMessage requestLogMessage) {
      StringBuilder builder = new StringBuilder();
      builder.append("\u8bf7\u6c42: \n\t");
      builder.append(this.retryContent(requestLogMessage));
      builder.append(this.proxyContent(requestLogMessage));
      builder.append(this.requestTypeChangeHistory(requestLogMessage));
      builder.append(requestLogMessage.getRequestLine());
      String headers = this.requestLoggingHeaders(requestLogMessage);
      if (StringUtils.isNotEmpty(headers)) {
         builder.append("\n\t\u8bf7\u6c42\u5934: \n");
         builder.append(headers);
      }

      String body = this.requestLoggingBody(requestLogMessage);
      if (StringUtils.isNotEmpty(body)) {
         builder.append("\n\t\u8bf7\u6c42\u4f53: \n");
         builder.append(body);
      }

      return builder.toString();
   }

   protected String responseLoggingContent(ResponseLogMessage responseLogMessage) {
      ForestResponse response = responseLogMessage.getResponse();
      if (response != null && response.getException() != null) {
         return "[\u7f51\u7edc\u9519\u8bef]: " + response.getException().getMessage();
      } else {
         int status = responseLogMessage.getStatus();
         long time = responseLogMessage.getTime();
         return status >= 0 ? "\u8bf7\u6c42\u54cd\u5e94: \u72b6\u6001\u7801: " + status + ", \u8017\u65f6: " + time + "ms" : "[\u7f51\u7edc\u9519\u8bef]: \u672a\u77e5\u7684\u7f51\u7edc\u9519\u8bef!";
      }
   }
}
