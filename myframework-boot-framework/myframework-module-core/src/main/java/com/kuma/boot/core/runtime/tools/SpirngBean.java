package com.kuma.boot.core.runtime.tools;

import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.Banner;
import org.springframework.boot.web.server.context.WebServerGracefulShutdownLifecycle;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * SpirngBean
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class SpirngBean {

    //1. 应用程序参数
    //SpringBoot程序在启动时，可以通过如下方式设置启动参数：
    //java -jar app.jar --pack.title=xxx --pack.version=1.0.0
    @Resource
    private Environment env;

    public void getArgs() {
        String title = env.getProperty("pack.title");
        String version = env.getProperty("pack.version");
    }

    @Resource
    private ApplicationArguments applicationArguments;

    public void getArgs1() {
        List<String> titles = applicationArguments.getOptionValues("pack.title");
        List<String> version = applicationArguments.getOptionValues("pack.version");

        String[] args = applicationArguments.getSourceArgs();
    }

    @Resource
    private Banner banner;

    //@Resource
    //private Environment env ;
    public void printBanner() {
        //banner.printBanner(env, PackApplication.class, System.out) ;
    }

    @Resource
    private ConversionService conversionService;

    public void xx() {
        Integer ret = (Integer) conversionService.convert("6666",
                TypeDescriptor.valueOf(String.class),
                TypeDescriptor.valueOf(Integer.class));
    }

    //7. 优雅关闭服务
    //
    //
    //如果你是内嵌Tomcat启动（以Jar包形式）SpringBoot项目，那么会向容器中注册一个WebServerGracefulShutdownLifecycle对象，通过该对象你可以优雅的关闭服务。
    //注意：你还需要开启如下配置
    //
    //server:
    //  shutdown: graceful
    @Resource
    private WebServerGracefulShutdownLifecycle webServerGracefullShutdown;

    public void shutdownWebServer() {
        webServerGracefullShutdown.stop(() -> {
            System.out.println("优雅关闭Web Server");
            //context.close() ;
        });
    }

}
