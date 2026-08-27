package com.kuma.cloud.project6.api;

/**
 * HelloService —— RPC 服务接口（服务端与客户端共用）
 *
 * <p>在真实项目中，此接口通常单独放在 api 模块（jar）中，
 * 服务端和客户端分别依赖该 jar。
 */
public interface HelloService {

    /**
     * 打招呼
     *
     * @param name 姓名
     * @return 问候语
     */
    String hello(String name);

    /**
     * 求和（演示带参数的 RPC 方法）
     *
     * @param a 加数
     * @param b 被加数
     * @return 求和结果
     */
    int add(int a, int b);
}
