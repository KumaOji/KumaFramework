package com.kuma.boot.core.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SpringBoot内置性能监控工具
 *  方法调用跟踪 (基于内置的Spring框架默认为性能监控提供的几个基本工具类)
 *
 *  SimpleTraceInterceptor拦截器使用也是非常的简单，该拦截主要就是用来跟踪方目标方法执行前后（或发生异常时）进行日志的记录。
 *  可用来跟踪方法的调用情况（方法调用前后或异常发生时）
 * &#064;FunctionTrace
 * &#064;GetMapping("/index")
 * public Object index() throws Exception {
 * int sleep = new Random().nextInt(2000);
 * // 模拟耗时  TimeUnit.MILLISECONDS.sleep(sleep) ;
 * return String.format("业务处理耗时: %d", sleep) ;
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FunctionTrace {

}
