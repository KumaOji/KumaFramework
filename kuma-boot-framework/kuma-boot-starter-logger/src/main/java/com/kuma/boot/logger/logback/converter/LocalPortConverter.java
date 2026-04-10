package com.kuma.boot.logger.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.HashSet;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.QueryExp;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPortConverter extends ClassicConverter {
   private static final Logger logger = LoggerFactory.getLogger(LocalPortConverter.class);
   private static String webPort;

   public LocalPortConverter() {
   }

   public String convert(ILoggingEvent event) {
      return webPort;
   }

   static {
      try {
         for(MBeanServer server : MBeanServerFactory.findMBeanServer((String)null)) {
            Set<ObjectName> names = new HashSet();
            names.addAll(server.queryNames(new ObjectName("Catalina:type=Connector,*"), (QueryExp)null));

            for(ObjectName oName : names) {
               String pValue = (String)server.getAttribute(oName, "protocol");
               if (StringUtils.equals("HTTP/1.1", pValue)) {
                  webPort = ObjectUtils.toString(server.getAttribute(oName, "port"));
               }
            }
         }
      } catch (Exception e) {
         logger.error("\u83b7\u53d6port\u5931\u8d25,\u5f71\u54cdlogback\u7684\u6587\u4ef6\u62fc\u63a5", e);
         webPort = null;
      }

   }
}
