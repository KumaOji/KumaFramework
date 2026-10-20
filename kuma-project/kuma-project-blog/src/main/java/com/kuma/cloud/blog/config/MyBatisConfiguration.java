package com.kuma.cloud.blog.config;

import com.kuma.cloud.blog.mapper.ArticleMapper;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisAutoConfiguration;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusAutoConfiguration;
import com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusInterceptorAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@MapperScan(basePackageClasses = ArticleMapper.class)
@ImportAutoConfiguration({MybatisAutoConfiguration.class, MybatisPlusAutoConfiguration.class,
        MybatisPlusInterceptorAutoConfiguration.class})
public class MyBatisConfiguration {}
