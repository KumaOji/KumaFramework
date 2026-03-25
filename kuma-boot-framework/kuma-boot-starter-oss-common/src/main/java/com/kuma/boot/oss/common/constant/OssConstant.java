package com.kuma.boot.oss.common.constant;

import java.util.concurrent.TimeUnit;

public class OssConstant {
   public static final String OSS = "kuma.boot.oss";
   public static final String ENABLE = "enable";
   public static final String DEFAULT_ENABLE_VALUE = "true";
   public static final int KB = 1024;
   public static final Long DEFAULT_PART_SIZE = 5242880L;
   public static final int DEFAULT_BUFFER_SIZE = 8192;
   public static final Long DEFAULT_PART_NUM = 10000L;
   public static final Integer DEFAULT_TASK_NUM = Runtime.getRuntime().availableProcessors();
   public static final Long DEFAULT_CONNECTION_TIMEOUT;

   static {
      DEFAULT_CONNECTION_TIMEOUT = TimeUnit.MINUTES.toMillis(5L);
   }

   public interface OssType {
      String LOCAL = "local";
      String FTP = "ftp";
      String SFTP = "sftp";
      String ALI = "ali";
      String QINIU = "qiniu";
      String MINIO = "minio";
      String BAIDU = "baidu";
      String TENCENT = "tencent";
      String HUAWEI = "huawei";
      String JD = "jd";
      String UP = "up";
      String JINSHAN = "jinshan";
      String WANGYI = "wangyi";
      String UCLOUD = "ucloud";
      String PINGAN = "pingan";
      String QINGYUN = "qingyun";
      String JDBC = "jdbc";
      String AWS = "aws";
   }
}
