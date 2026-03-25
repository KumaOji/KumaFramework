package com.kuma.boot.sms.common.enums;

public enum SmsType {
   ALIYUN,
   BAIDUCLOUD,
   CHINAMOBILE,
   HUAWEICLOUD,
   JDCLOUD,
   JPUSH,
   NETEASE,
   QCLOUD,
   QCLOUDV3,
   QINIU,
   UPYUN,
   YUNPIAN;

   // $FF: synthetic method
   private static SmsType[] $values() {
      return new SmsType[]{ALIYUN, BAIDUCLOUD, CHINAMOBILE, HUAWEICLOUD, JDCLOUD, JPUSH, NETEASE, QCLOUD, QCLOUDV3, QINIU, UPYUN, YUNPIAN};
   }
}
