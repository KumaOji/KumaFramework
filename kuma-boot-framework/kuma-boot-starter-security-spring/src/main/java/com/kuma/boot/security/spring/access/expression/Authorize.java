package com.kuma.boot.security.spring.access.expression;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 方法/类级别的权限校验注解，底层通过 Spring Security {@code @PreAuthorize} 驱动。
 *
 * <p>支持单权限和多权限两种模式：
 * <pre>{@code
 * // 单权限：当前用户必须拥有 ROLE_ADMIN
 * @Authorize("ROLE_ADMIN")
 *
 * // 多权限 OR（默认）：拥有其中任意一个即可访问
 * @Authorize({"ROLE_ADMIN", "ROLE_OPS"})
 *
 * // 多权限 AND：必须同时拥有所有权限才能访问
 * @Authorize(value = {"ROLE_ADMIN", "ROLE_AUDITOR"}, logical = Authorize.Logical.AND)
 * }</pre>
 *
 * <p>标注在类上时，对该类所有方法生效；方法上的注解优先级高于类上的注解。
 *
 * @see AuthorizeCheckService
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("@authorizeCheck.checkAuthority(#root)")
public @interface Authorize {

    /**
     * 所需权限列表，至少指定一个。
     *
     * <p>单个权限可直接写字符串：{@code @Authorize("ROLE_ADMIN")}
     */
    String[] value();

    /**
     * 多权限之间的逻辑关系，默认 {@link Logical#OR}。
     *
     * <ul>
     *   <li>{@link Logical#OR}：用户拥有列表中任意一个权限即可（宽松校验）</li>
     *   <li>{@link Logical#AND}：用户必须同时拥有列表中所有权限（严格校验）</li>
     * </ul>
     */
    Logical logical() default Logical.OR;

    /**
     * 多权限的逻辑关系。
     */
    enum Logical {
        /** 任意一个权限满足即可（OR） */
        OR,
        /** 所有权限都必须满足（AND） */
        AND
    }
}
