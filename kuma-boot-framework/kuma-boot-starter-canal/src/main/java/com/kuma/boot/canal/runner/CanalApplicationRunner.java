package com.kuma.boot.canal.runner;

import com.kuma.boot.canal.client.CanalClient;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Objects;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class CanalApplicationRunner implements ApplicationRunner {
   private final CanalClient canalClient;

   public CanalApplicationRunner(CanalClient canalClient) {
      this.canalClient = canalClient;
   }

   public void run(ApplicationArguments applicationArguments) throws Exception {
      if (Objects.nonNull(this.canalClient)) {
         try {
            LogUtils.info(" CanalClient 正在尝试开启 canal 客户端....", new Object[0]);
            this.canalClient.start();
            LogUtils.info(" CanalClient 启动 canal 客户端成功....", new Object[0]);
         } catch (Exception e) {
            LogUtils.error(e);
         }
      }

   }
}
