package com.kuma.boot.client.forest.config;

import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.logging.ResponseLogMessage;
import com.dtflys.forest.utils.StringUtils;

public class MyLogHandler extends DefaultLogHandler {
   public MyLogHandler() {
   }

   public void logContent(String content) {
      super.logContent("[哈哈，这是我自己的日志]: " + content);
   }

   protected String requestLoggingContent(RequestLogMessage requestLogMessage) {
      StringBuilder builder = new StringBuilder();
      builder.append("请求: \n\t");
      builder.append(this.retryContent(requestLogMessage));
      builder.append(this.proxyContent(requestLogMessage));
      builder.append(this.requestTypeChangeHistory(requestLogMessage));
      builder.append(requestLogMessage.getRequestLine());
      String headers = this.requestLoggingHeaders(requestLogMessage);
      if (StringUtils.isNotEmpty(headers)) {
         builder.append("\n\t请求头: \n");
         builder.append(headers);
      }

      String body = this.requestLoggingBody(requestLogMessage);
      if (StringUtils.isNotEmpty(body)) {
         builder.append("\n\t请求体: \n");
         builder.append(body);
      }

      return builder.toString();
   }

   protected String responseLoggingContent(ResponseLogMessage responseLogMessage) {
      ForestResponse response = responseLogMessage.getResponse();
      if (response != null && response.getException() != null) {
         return "[网络错误]: " + response.getException().getMessage();
      } else {
         int status = responseLogMessage.getStatus();
         long time = responseLogMessage.getTime();
         return status >= 0 ? "请求响应: 状态码: " + status + ", 耗时: " + time + "ms" : "[网络错误]: 未知的网络错误!";
      }
   }
}
