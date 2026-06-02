package com.kuma.boot.oss.cos.support;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.kuma.boot.oss.common.condition.ConditionalOnOssEnabled;
import com.kuma.boot.oss.common.service.StandardOssClient;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 腾讯云 COS 自动配置
 *
 * @author kuma
 */
@AutoConfiguration
@ConditionalOnOssEnabled
@EnableConfigurationProperties({CosOssProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.oss",
   name = {"type"},
   havingValue = "tencent"
)
public class CosOssConfiguration implements InitializingBean {
   public static final String DEFAULT_BEAN_NAME = "cosOssClient";
   private final CosOssProperties cosOssProperties;

   public CosOssConfiguration(CosOssProperties cosOssProperties) {
      this.cosOssProperties = cosOssProperties;
   }

   @Bean
   @ConditionalOnMissingBean
   public StandardOssClient cosOssClient(CosOssConfig cosOssConfig) {
      return new CosOssClient(this.cosClient(cosOssConfig), cosOssConfig);
   }

   public COSClient cosClient(CosOssConfig cosOssConfig) {
      COSCredentials credentials = new BasicCOSCredentials(cosOssConfig.getAccessKey(), cosOssConfig.getSecretKey());
      ClientConfig clientConfig = new ClientConfig(new Region(cosOssConfig.getRegion()));
      CosOssClientConfig config = cosOssConfig.getClientConfig();
      if (ObjUtil.isNotEmpty(config)) {
         clientConfig.setConnectionTimeout(config.getConnectTimeout().intValue());
         clientConfig.setSocketTimeout(config.getSocketTimeout().intValue());
         clientConfig.setMaxConnectionsCount(config.getMaxConnections());
      }

      return new COSClient(credentials, clientConfig);
   }

   @Override
   public void afterPropertiesSet() {
      Map<String, CosOssConfig> cosOssConfigMap = this.cosOssProperties.getOssConfig();
      if (cosOssConfigMap.isEmpty()) {
         SpringUtil.registerBean(DEFAULT_BEAN_NAME, this.cosOssClient(this.cosOssProperties));
      } else {
         String region = this.cosOssProperties.getRegion();
         String accessKey = this.cosOssProperties.getAccessKey();
         String secretKey = this.cosOssProperties.getSecretKey();
         CosOssClientConfig clientConfig = this.cosOssProperties.getClientConfig();
         cosOssConfigMap.forEach((name, cosOssConfig) -> {
            if (ObjUtil.isEmpty(cosOssConfig.getRegion())) {
               cosOssConfig.setRegion(region);
            }

            if (ObjUtil.isEmpty(cosOssConfig.getAccessKey())) {
               cosOssConfig.setAccessKey(accessKey);
            }

            if (ObjUtil.isEmpty(cosOssConfig.getSecretKey())) {
               cosOssConfig.setSecretKey(secretKey);
            }

            if (ObjUtil.isEmpty(cosOssConfig.getClientConfig())) {
               cosOssConfig.setClientConfig(clientConfig);
            }

            SpringUtil.registerBean(name, this.cosOssClient(cosOssConfig));
         });
      }
   }
}
