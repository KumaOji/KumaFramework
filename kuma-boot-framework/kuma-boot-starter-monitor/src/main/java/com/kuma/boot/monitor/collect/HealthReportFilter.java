package com.kuma.boot.monitor.collect;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.servlet.ResponseUtils;
import com.kuma.boot.monitor.autoconfigure.properties.DumpProperties;
import com.kuma.boot.monitor.model.Report;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.util.StringUtils;

public class HealthReportFilter implements Filter {
   public HealthReportFilter() {
   }

   public void init(FilterConfig filterConfig) throws ServletException {
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest)servletRequest;
      HttpServletResponse response = (HttpServletResponse)servletResponse;
      String contextPath = StringUtils.trimTrailingCharacter(request.getContextPath(), '/');
      String uri = request.getRequestURI();
      HealthCheckProvider healthProvider = (HealthCheckProvider)ContextUtils.getBean(HealthCheckProvider.class, true);
      DumpProperties dumpProperties = (DumpProperties)ContextUtils.getBean(DumpProperties.class, true);
      if (Objects.nonNull(healthProvider) && Objects.nonNull(dumpProperties) && uri.startsWith(contextPath + "/health/report")) {
         try {
            boolean isAnalyse = !"false".equalsIgnoreCase(request.getParameter("isAnalyse"));
            Report report = healthProvider.getReport(isAnalyse);
            if (request.getContentType() != null && request.getContentType().contains("json")) {
               response.setHeader("Content-type", "application/json;charset=UTF-8");
               String html = report.toJson();
               ResponseUtils.success(response, html);
               return;
            }

            response.setHeader("Content-type", "text/html;charset=UTF-8");
            String html = report.toHtml().replace("\r\n", "<br/>").replace("\n", "<br/>").replace("/n", "\n").replace("/r", "\r");
            if (dumpProperties.isEnabled()) {
               html = "dump\u4fe1\u606f:<a href='/health/dump/'>\u67e5\u770b</a><br/>" + html;
            }

            response.setCharacterEncoding("UTF-8");
            response.getWriter().append(html);
            response.getWriter().flush();
            response.getWriter().close();
         } catch (Exception e) {
            LogUtils.error(e, "kuma-boot-starter-monitor", new Object[]{"/health/report\u6253\u5f00\u51fa\u9519"});
            response.getWriter().close();
         }
      }

   }

   public void destroy() {
   }
}
