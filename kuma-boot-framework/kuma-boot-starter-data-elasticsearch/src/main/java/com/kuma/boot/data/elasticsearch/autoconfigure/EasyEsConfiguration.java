package com.kuma.boot.data.elasticsearch.autoconfigure;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@AutoConfiguration
@ConditionalOnProperty(
   value = {"easy-es.enable"},
   havingValue = "true"
)
@EsMapperScan({"com.kuma.cloud.*.biz.**.esmapper"})
public class EasyEsConfiguration {
   public EasyEsConfiguration() {
   }
}
