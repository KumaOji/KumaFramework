package com.kuma.boot.data.datasource.autoconfigure;

import com.baomidou.dynamic.datasource.processor.DsJakartaHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsJakartaSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.config.DsLastParamProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/** 为 Nacos/YAML 和数据库配置来源提供同一条数据源解析链。 */
@AutoConfiguration(beforeName = "com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration")
@ConditionalOnClass(DsProcessor.class)
public class DataSourceProcessorAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DsProcessor dsProcessor() {
        var last = new DsLastParamProcessor();
        var header = new DsJakartaHeaderProcessor();
        var session = new DsJakartaSessionProcessor();
        last.setNextProcessor(header);
        header.setNextProcessor(session);
        session.setNextProcessor(new DsSpelExpressionProcessor());
        return last;
    }
}
