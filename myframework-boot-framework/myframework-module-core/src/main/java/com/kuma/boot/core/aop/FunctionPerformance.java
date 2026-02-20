package com.kuma.boot.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SpringBoot内置性能监控工具
 *  性能监控拦截器 (基于内置的Spring框架默认为性能监控提供的几个基本工具类)
 *
 *  可用来记录方法执行的耗时情况
 * &#064;FunctionPerformance
 * &#064;GetMapping("/index")
 * public Object index() throws Exception {
 * int sleep = new Random().nextInt(2000);
 * // 模拟耗时  TimeUnit.MILLISECONDS.sleep(sleep) ;
 * return String.format("业务处理耗时: %d", sleep) ;
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FunctionPerformance {

}
