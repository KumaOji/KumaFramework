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
            LogUtils.info(" CanalClient \u6b63\u5728\u5c1d\u8bd5\u5f00\u542f canal \u5ba2\u6237\u7aef....", new Object[0]);
            this.canalClient.start();
            LogUtils.info(" CanalClient \u542f\u52a8 canal \u5ba2\u6237\u7aef\u6210\u529f....", new Object[0]);
         } catch (Exception e) {
            LogUtils.error(e);
         }
      }

   }
}
