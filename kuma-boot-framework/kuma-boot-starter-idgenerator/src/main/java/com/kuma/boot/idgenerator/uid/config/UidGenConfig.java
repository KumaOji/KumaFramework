package com.kuma.boot.idgenerator.uid.config;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ConditionalOnClass({MapperScan.class, SqlSession.class})
@ComponentScan(
   basePackages = {"com.kuma.boot.idgenerator.uid"}
)
@EnableConfigurationProperties({UidGenProperties.class})
@MapperScan({"com.kuma.boot.idgenerator.uid.worker.dao"})
public class UidGenConfig {
   public UidGenConfig() {
   }
}
