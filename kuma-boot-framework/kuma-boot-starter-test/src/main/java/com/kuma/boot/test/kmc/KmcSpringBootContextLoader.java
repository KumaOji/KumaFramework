package com.kuma.boot.test.kmc;

import com.kuma.boot.core.banner.KmcBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class KmcSpringBootContextLoader extends SpringBootContextLoader {
   public KmcSpringBootContextLoader() {
   }

   protected SpringApplication getSpringApplication() {
      SpringApplication springApplication = super.getSpringApplication();
      ResourceLoader resourceLoader = new DefaultResourceLoader();
      Resource resource = resourceLoader.getResource("kmc-banner.txt");
      springApplication.setBanner(new KmcBanner(resource));
      return springApplication;
   }
}
