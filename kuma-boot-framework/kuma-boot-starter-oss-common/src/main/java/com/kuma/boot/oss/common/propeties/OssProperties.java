package com.kuma.boot.oss.common.propeties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.oss"
)
public class OssProperties {
   public static final String PREFIX = "kuma.boot.oss";
   private Boolean enabled = false;
   private DFSTypeEnum type;

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public DFSTypeEnum getType() {
      return this.type;
   }

   public void setType(DFSTypeEnum type) {
      this.type = type;
   }

   public static enum DFSTypeEnum {
      ALIYUN,
      AWS,
      BAIDU,
      FASTDFS,
      FTP,
      HUAWEI,
      JD,
      JDBC,
      MINIO,
      NGINX,
      PINGAN,
      QINIU,
      QINGYUN,
      SFTP,
      JINSHAN,
      TENCENT,
      UCLOUD,
      UP,
      WANGYI,
      LOCAL;

      // $FF: synthetic method
      private static DFSTypeEnum[] $values() {
         return new DFSTypeEnum[]{ALIYUN, AWS, BAIDU, FASTDFS, FTP, HUAWEI, JD, JDBC, MINIO, NGINX, PINGAN, QINIU, QINGYUN, SFTP, JINSHAN, TENCENT, UCLOUD, UP, WANGYI, LOCAL};
      }
   }
}
