package com.kuma.boot.idempotent.idempotentenhance.adapter.mysql.config;

import com.kuma.boot.idempotent.idempotentenhance.adapter.mysql.MySqlIdempotentRepositoryImpl;
import com.kuma.boot.idempotent.idempotentenhance.adapter.mysql.mapper.BusinessIdempotentMapper;
import com.kuma.boot.idempotent.idempotentenhance.core.config.IdempotentCoreAutoConfiguration;
import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jspecify.annotations.NonNull;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 自动配置
 *
 * @author wenpanfeng 2023/01/04 18:05
 */
@AutoConfiguration(after = IdempotentCoreAutoConfiguration.class)
@EnableConfigurationProperties({IdempotentMysqlAdapterProperties.class})
@ConditionalOnClass({MapperScan.class, SqlSessionFactory.class})
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(
        prefix = IdempotentMysqlAdapterProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
@MapperScan(value = {"com.kuma.boot.idempotent.idempotentenhance.adapter.mysql.mapper"})
public class IdempotentAdapterDbAutoConfiguration {

    @Bean(name = "mySqlIdempotentRepository")
    @ConditionalOnMissingBean(name = "mySqlIdempotentRepository")
    public MySqlIdempotentRepositoryImpl mySqlIdempotentRepository(@NonNull BusinessIdempotentMapper businessIdempotentMapper) {
        return new MySqlIdempotentRepositoryImpl(businessIdempotentMapper);
    }

}
