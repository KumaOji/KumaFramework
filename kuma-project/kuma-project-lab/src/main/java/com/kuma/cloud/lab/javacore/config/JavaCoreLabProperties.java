package com.kuma.cloud.lab.javacore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Java 基础知识实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.javacore")
public class JavaCoreLabProperties {

    /**
     * Socket 回显服务监听端口。
     */
    private int socketPort = 19_091;

    /**
     * Socket 客户端读写超时（毫秒）。
     */
    private int socketTimeoutMillis = 3_000;

    /**
     * 文件实验工作目录（相对或绝对路径）。
     */
    private String fileWorkspace = "data/lab-javacore";

}
