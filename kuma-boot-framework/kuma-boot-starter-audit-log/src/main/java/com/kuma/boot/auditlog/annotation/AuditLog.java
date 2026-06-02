/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.auditlog.annotation;

import com.kuma.boot.auditlog.enums.OperateType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作审计日志注解.
 *
 * <p>标注在需要记录"谁在何时改了什么"的业务方法上，由切面自动采集操作人、时间、
 * 入参 / 出参、客户端 IP、耗时、成功与否等信息并落库 / 落日志。
 *
 * <p>{@link #description()} 与 {@link #bizId()} 支持 SpEL 表达式，可引用方法参数：
 * <pre>{@code
 *   @AuditLog(module = "文章", type = OperateType.UPDATE,
 *             description = "'修改文章:' + #dto.title", bizId = "#dto.id")
 *   public void update(ArticleDTO dto) { ... }
 * }</pre>
 *
 * @author kuma
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /** 操作描述，支持 SpEL（如 {@code "'删除用户:' + #id"}）；为纯文本时原样记录. */
    String description() default "";

    /** 业务模块名（如 "用户管理"、"订单"）. */
    String module() default "";

    /** 操作类型. */
    OperateType type() default OperateType.OTHER;

    /** 业务主键，支持 SpEL（如 {@code "#dto.id"}），便于按业务对象检索审计记录. */
    String bizId() default "";

    /** 是否记录方法入参. */
    boolean recordParams() default true;

    /** 是否记录方法返回值. */
    boolean recordResult() default true;
}
