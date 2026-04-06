package com.kuma.boot.seata.autoconfigure;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.datasource.init.StandardDatabaseScript;
import com.kuma.boot.seata.autoconfigure.properties.SeataProperties;
import com.kuma.boot.seata.database.SeataSqlScripter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.sql.DataSource;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
@EnableAutoDataSourceProxy
@EnableConfigurationProperties({SeataProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.seata",
   name = {"enabled"},
   havingValue = "true"
)
public class SeataAutoConfiguration implements InitializingBean {
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(SeataAutoConfiguration.class, "kuma-boot-starter-seata", new String[0]);
   }

   @Bean
   public SeataXidFilter seataXidFilter() {
      return new SeataXidFilter();
   }

   @Bean
   @ConditionalOnClass({DataSource.class, StandardDatabaseScript.class})
   @ConditionalOnBean({DataSource.class})
   public SeataSqlScripter seataSqlScripter() {
      return new SeataSqlScripter();
   }

   public static class SeataXidFilter extends OncePerRequestFilter {
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         String restXid = request.getHeader("xid");
         if (StrUtil.isNotBlank(restXid)) {
            RootContext.bind(restXid);
            LogUtils.info("bind[" + restXid + "] to RootContext", new Object[0]);
         }

         filterChain.doFilter(request, response);
      }
   }
}
