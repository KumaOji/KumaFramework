package com.kuma.boot.oss.common.constant;

public enum OssType {
   LOCAL("local"),
   FTP("ftp"),
   SFTP("sftp"),
   ALI("ali"),
   QINIU("qiniu"),
   MINIO("minio"),
   BAIDU("baidu"),
   TENCENT("tencent"),
   HUAWEI("huawei"),
   JD("jd"),
   UP("up"),
   JINSHAN("jinshan"),
   WANGYI("wangyi"),
   UCLOUD("ucloud"),
   PINGAN("pingan"),
   QINGYUN("qingyun"),
   JDBC("jdbc"),
   AWS("aws");

   private final String value;

   private OssType(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }

   // $FF: synthetic method
   private static OssType[] $values() {
      return new OssType[]{LOCAL, FTP, SFTP, ALI, QINIU, MINIO, BAIDU, TENCENT, HUAWEI, JD, UP, JINSHAN, WANGYI, UCLOUD, PINGAN, QINGYUN, JDBC, AWS};
   }
}
