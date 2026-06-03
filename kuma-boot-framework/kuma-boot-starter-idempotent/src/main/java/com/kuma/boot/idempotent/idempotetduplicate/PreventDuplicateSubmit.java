package com.kuma.boot.idempotent.idempotetduplicate;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求信息日志
 *
 * @author : YiFei
 * @since : 2023/10/2 17:31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PreventDuplicateSubmit {

    DuplicateTypeEnum type() default DuplicateTypeEnum.IP;

    /**
     * 是否为全局
     * 默认 : ip:classSimpleName:methodName
     * 全局 : ip
     */
    boolean global() default false;


    /**
     * 防重提交锁过期时间(秒)
     * <p>
     * 默认1秒内不允许重复提交
     */
    int expire() default 1;

}
