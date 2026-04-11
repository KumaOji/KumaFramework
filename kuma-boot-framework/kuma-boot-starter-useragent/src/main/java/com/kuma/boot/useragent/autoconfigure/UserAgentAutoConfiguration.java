package com.kuma.boot.useragent.autoconfigure;

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.useragent.autoconfigure.properties.UserAgentProperties;
import com.kuma.boot.useragent.browscap.BrowscapUserAgentConverter;
import com.kuma.boot.useragent.yauaa.YauaaUserAgentConverter;
import java.io.IOException;
import java.util.Arrays;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@EnableConfigurationProperties({UserAgentProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.useragent",
   name = {"enabled"},
   havingValue = "true"
)
@AutoConfiguration(
   before = {AgentAutoConfiguration.class}
)
@ImportAutoConfiguration({AgentAutoConfiguration.class})
public class UserAgentAutoConfiguration implements InitializingBean {
   public UserAgentAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(UserAgentAutoConfiguration.class, "kuma-boot-starter-useragent", new String[0]);
   }

   @AutoConfiguration
   @ConditionalOnClass({UserAgentParser.class})
   public static class BrowscapAutoConfiguration {
      public BrowscapAutoConfiguration() {
      }

      @Bean
      public UserAgentParser userAgentParser() throws IOException, ParseException {
         return (new UserAgentService()).loadParser(Arrays.asList(BrowsCapField.values()));
      }

      @Bean
      @ConditionalOnMissingBean
      public BrowscapUserAgentConverter browscapUserAgentConverter(UserAgentParser userAgentAnalyzer) {
         return new BrowscapUserAgentConverter(userAgentAnalyzer);
      }
   }

   @AutoConfiguration
   @ConditionalOnClass({UserAgentAnalyzer.class})
   public static class YauaaAutoConfiguration {
      public YauaaAutoConfiguration() {
      }

      @Bean
      @Scope("prototype")
      public UserAgentAnalyzer userAgentAnalyzer() {
         return (UserAgentAnalyzer)((UserAgentAnalyzer.UserAgentAnalyzerBuilder)((UserAgentAnalyzer.UserAgentAnalyzerBuilder)UserAgentAnalyzer.newBuilder().hideMatcherLoadStats()).withCache(10000)).build();
      }

      @Bean
      @ConditionalOnMissingBean
      public YauaaUserAgentConverter yauaaUserAgentConverter(UserAgentAnalyzer userAgentAnalyzer) {
         return new YauaaUserAgentConverter(userAgentAnalyzer);
      }
   }
}
