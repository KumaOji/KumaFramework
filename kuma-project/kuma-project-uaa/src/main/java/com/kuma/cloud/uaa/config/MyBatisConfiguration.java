package com.kuma.cloud.uaa.config;

import com.kuma.cloud.uaa.mapper.UaaUserMapper;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisAutoConfiguration;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusAutoConfiguration;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusInterceptorAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@MapperScan(basePackageClasses = UaaUserMapper.class)
@ImportAutoConfiguration({MybatisAutoConfiguration.class, MybatisPlusAutoConfiguration.class,
        MybatisPlusInterceptorAutoConfiguration.class})
public class MyBatisConfiguration {}
