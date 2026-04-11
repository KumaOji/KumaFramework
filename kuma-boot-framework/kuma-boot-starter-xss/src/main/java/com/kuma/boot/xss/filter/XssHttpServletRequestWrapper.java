package com.kuma.boot.xss.filter;

import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
   private static final Logger log = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);

   public XssHttpServletRequestWrapper(HttpServletRequest request) {
      super(request);
   }

   private String cleaning(String value) {
      return XssUtils.cleaning(value);
   }

   private String[] cleaning(String[] parameters) {
      List<String> cleanParameters = Arrays.stream(parameters).map(XssUtils::cleaning).toList();
      String[] results = new String[cleanParameters.size()];
      return (String[])cleanParameters.toArray(results);
   }

   public String getHeader(String name) {
      String header = super.getHeader(name);
      return StringUtils.isBlank(header) ? header : this.cleaning(header);
   }

   public String getParameter(String name) {
      String header = super.getHeader(name);
      return StringUtils.isBlank(header) ? header : this.cleaning(header);
   }

   public String[] getParameterValues(String name) {
      String[] parameterValues = super.getParameterValues(name);
      return ArrayUtils.isNotEmpty(parameterValues) ? this.cleaning(parameterValues) : super.getParameterValues(name);
   }

   public Map<String, String[]> getParameterMap() {
      Map<String, String[]> parameterMap = super.getParameterMap();
      return (Map)parameterMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> this.cleaning((String[])entry.getValue())));
   }
}
