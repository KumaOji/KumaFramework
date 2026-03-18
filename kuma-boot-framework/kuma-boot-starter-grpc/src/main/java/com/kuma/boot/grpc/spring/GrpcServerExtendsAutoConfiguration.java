package com.kuma.boot.grpc.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;

@AutoConfiguration
public class GrpcServerExtendsAutoConfiguration {
   @Configuration
   @ConditionalOnClass({SecurityConfigurerAdapter.class})
   public static class GrpcAuthenticationConfiguration {
      @Bean
      @GlobalServerInterceptor
      AuthenticationProcessInterceptor jwtSecurityFilterChain(GrpcSecurity grpc) throws Exception {
         return (AuthenticationProcessInterceptor)grpc.authorizeRequests((requests) -> requests.allRequests().permitAll()).build();
      }
   }
}
