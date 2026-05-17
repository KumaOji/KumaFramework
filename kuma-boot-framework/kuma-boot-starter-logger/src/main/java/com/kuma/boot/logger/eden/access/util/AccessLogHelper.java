package com.kuma.boot.logger.eden.access.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.kuma.boot.core.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.logger.eden.IpConfigUtils;
import com.kuma.boot.logger.eden.ServletUtils;
import com.kuma.boot.logger.eden.model.AccessLog;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class AccessLogHelper {
   public static final String ACCESS_LOG = "AccessLog";
   public static final Logger log = LoggerFactory.getLogger("AccessLog");

   public AccessLogHelper() {
   }

   public static boolean shouldLog(double sampleRate) {
      return sampleRate >= (double)1.0F || Math.random() < sampleRate;
   }

   public static void log(MethodInvocation invocation, Object result, Throwable throwable, long duration, boolean enabledMdc, int maxLength, long slowThreshold) {
      AccessLog accessLog = new AccessLog();
      accessLog.setThrowable(throwable);
      accessLog.setDuration(duration);
      String className = Objects.requireNonNull(invocation.getThis()).getClass().getName();
      String methodName = invocation.getMethod().getName();
      String location = className + "." + methodName;
      accessLog.setLocation(location);
      Object[] args = invocation.getArguments();
      StringBuilder argsBuilder = new StringBuilder();

      for(int i = 0; i < args.length; ++i) {
         if (i > 0) {
            argsBuilder.append(", ");
         }

         argsBuilder.append(args[i] == null ? PropertyUtils.NULL : args[i].toString());
      }

      String arguments = argsBuilder.toString();
      if (arguments.length() > maxLength) {
         arguments = arguments.substring(0, maxLength);
      }

      accessLog.setArguments(arguments);
      String returnValue = StringUtils.isEmpty(result) ? "" : JacksonUtils.toJSONString(result);
      if (returnValue.length() > maxLength) {
         returnValue = returnValue.substring(0, maxLength);
      }

      accessLog.setReturnValue(returnValue);
      if (enabledMdc) {
         MDC.put("className", className);
         MDC.put("methodName", methodName);
         MDC.put("arguments", CharSequenceUtil.trimToEmpty(arguments));
         MDC.put("returnValue", CharSequenceUtil.trimToEmpty(returnValue));
         MDC.put("duration", String.valueOf(duration));
      }

      log(accessLog, slowThreshold);
   }

   public static void log(HttpServletRequest req, HttpServletResponse res, Throwable throwable, long duration, boolean enabledMdc, int maxLength, long slowThreshold) {
      AccessLog accessLog = new AccessLog();
      accessLog.setThrowable(throwable);
      accessLog.setDuration(duration);
      String remoteUser = ServletUtils.getRemoteUser();
      String remoteAddr = IpConfigUtils.parseIpAddress(req);
      String location = req.getRequestURI();
      accessLog.setLocation(location);
      String arguments = ServletUtils.getRequestBody(req);
      if (arguments != null && arguments.length() > maxLength) {
         arguments = arguments.substring(0, maxLength);
      }

      accessLog.setArguments(arguments);
      String returnValue = ServletUtils.getResponseBody(res);
      if (returnValue.length() > maxLength) {
         returnValue = returnValue.substring(0, maxLength);
      }

      accessLog.setReturnValue(returnValue);
      if (enabledMdc) {
         MDC.put("remoteUser", remoteUser);
         MDC.put("remoteAddr", remoteAddr);
         MDC.put("arguments", arguments);
         MDC.put("returnValue", returnValue);
         MDC.put("duration", String.valueOf(duration));
      }

      log(accessLog, slowThreshold);
   }

   public static void log(AccessLog accessLog, long slowThreshold) {
      StringBuilder sb = new StringBuilder();
      sb.append(accessLog.getLocation()).append("(").append(accessLog.getArguments()).append(")");
      if (accessLog.getThrowable() != null) {
         sb.append(" threw exception: ").append(accessLog.getThrowable()).append(" (").append(accessLog.getDuration()).append("ms)");
         log.error(sb.toString());
      } else {
         sb.append(" returned: ").append(accessLog.getReturnValue()).append(" (").append(accessLog.getDuration()).append("ms)");
         if (accessLog.getDuration() >= slowThreshold) {
            log.warn(sb.toString());
         } else {
            log.info(sb.toString());
         }
      }

   }
}
