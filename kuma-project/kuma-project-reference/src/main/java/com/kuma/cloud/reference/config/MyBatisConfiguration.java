package com.kuma.cloud.reference.config;

import com.kuma.cloud.reference.mapper.OrderMapper;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisAutoConfiguration;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusAutoConfiguration;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusInterceptorAutoConfiguration;
import com.kuma.boot.data.mybatis.interceptor.log.configuration.EnableActualSql;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableActualSql
@MapperScan(basePackageClasses = OrderMapper.class)
@ImportAutoConfiguration({MybatisAutoConfiguration.class, MybatisPlusAutoConfiguration.class,
        MybatisPlusInterceptorAutoConfiguration.class})
public class MyBatisConfiguration {}
