package com.kuma.boot.eventbus.disruptor.tmp5.enums;

public enum WaitStrategyEnum {
   SLEEP("SLEEP", "com.lmax.disruptor.SleepingWaitStrategy", "\u4e09\u6bb5\u5f0f\uff0c\u4e00\u9636\u6bb5\u81ea\u65cb\uff0c\u4e8c\u9636\u6bb5\u6267\u884cThread.yield\uff0c\u4e09\u9636\u6bb5\u7761\u7720\uff0c\u6027\u80fd\u8868\u73b0\u8ddfBlockingWaitStrategy\u5dee\u4e0d\u591a\uff0c\u5bf9CPU\u7684\u6d88\u8017\u4e5f\u7c7b\u4f3c\uff0c\u4f46\u5176\u5bf9\u751f\u4ea7\u8005\u7ebf\u7a0b\u7684\u5f71\u54cd\u6700\u5c0f\uff0c\u9002\u5408\u7528\u4e8e\u5f02\u6b65\u65e5\u5fd7\u7c7b\u4f3c\u7684\u573a\u666f"),
   YIELD("YIELD", "com.lmax.disruptor.YieldingWaitStrategy", "\u4e8c\u6bb5\u5f0f\uff0c\u4e00\u9636\u6bb5\u81ea\u65cb100\u6b21\uff0c\u4e8c\u9636\u6bb5\u6267\u884cThread.yield\uff0c\u9700\u8981\u4f4e\u5ef6\u8fdf\u7684\u573a\u666f\u53ef\u4f7f\u7528\u6b64\u7b56\u7565\uff0c\u662f\u53ef\u4ee5\u88ab\u7528\u5728\u4f4e\u5ef6\u8fdf\u7cfb\u7edf\u4e2d\u7684\u4e24\u4e2a\u7b56\u7565\u4e4b\u4e00\uff0c\u8fd9\u79cd\u7b56\u7565\u5728\u51cf\u4f4e\u7cfb\u7edf\u5ef6\u8fdf\u7684\u540c\u65f6\u4e5f\u4f1a\u589e\u52a0CPU\u8fd0\u7b97\u91cf"),
   BLOCK("BLOCK", "com.lmax.disruptor.BlockingWaitStrategy", "\u9ed8\u8ba4\u7b56\u7565(\u662f\u6700\u4f4e\u6548\u7684\u7b56\u7565)\u3002\u4f7f\u7528\u9501\u548cCondition \u7684\u7b49\u5f85\u3001\u5524\u9192\u673a\u5236\u3002\u901f\u5ea6\u6162\uff0c\u4f46\u8282\u7701CPU\u8d44\u6e90\u5e76\u4e14\u5728\u4e0d\u540c\u90e8\u7f72\u73af\u5883\u4e2d\u80fd\u63d0\u4f9b\u66f4\u52a0\u4e00\u81f4\u7684\u6027\u80fd\u8868\u73b0"),
   BUSYSPIN("BUSYSPIN", "com.lmax.disruptor.BusySpinWaitStrategy", "\u6027\u80fd\u6700\u9ad8\u7684\u7b56\u7565\uff0c\u4e0eYieldingWaitStrategy\u4e00\u6837\u5728\u4f4e\u5ef6\u8fdf\u573a\u666f\u4f7f\u7528\uff0c\u4f46\u662f\u6b64\u7b56\u7565\u8981\u6c42\u6d88\u8d39\u8005\u6570\u91cf\u4f4e\u4e8eCPU\u903b\u8f91\u5185\u6838\u603b\u6570"),
   TIMEOUT("TIMEOUT", "com.lmax.disruptor.TimeoutBlockingWaitStrategy", "\u52a0\u9501\uff0c\u6709\u8d85\u65f6\u9650\u5236CPU\u8d44\u6e90\u7d27\u7f3a\uff0c\u541e\u5410\u91cf\u548c\u5ef6\u8fdf\u5e76\u4e0d\u91cd\u8981\u7684\u573a\u666f");

   private String type;
   private String className;
   private String desc;

   private WaitStrategyEnum(String type, String className, String desc) {
      this.type = type;
      this.className = className;
      this.desc = desc;
   }

   public String getType() {
      return this.type;
   }

   public String getClassName() {
      return this.className;
   }

   public String getDesc() {
      return this.desc;
   }

   // $FF: synthetic method
   private static WaitStrategyEnum[] $values() {
      return new WaitStrategyEnum[]{SLEEP, YIELD, BLOCK, BUSYSPIN, TIMEOUT};
   }
}
