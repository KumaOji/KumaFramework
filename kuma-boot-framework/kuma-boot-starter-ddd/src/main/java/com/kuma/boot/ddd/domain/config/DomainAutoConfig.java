package com.kuma.boot.ddd.domain.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
@MapperScan({"com.kuma.boot.ddd.domain.mapper"})
public class DomainAutoConfig {
}
