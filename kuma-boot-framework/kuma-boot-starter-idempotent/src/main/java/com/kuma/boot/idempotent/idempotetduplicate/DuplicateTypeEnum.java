package com.kuma.boot.idempotent.idempotetduplicate;

/**
 * 防重复提交枚举
 *
 * @author YiFei
 * @since 2024/5/10 19:23
 */
public enum DuplicateTypeEnum {
    /**
     * userId:classSimpleName:methodName
     */
    USER_ID,
    /**
     * username:classSimpleName:methodName
     */
    USER_NAME,
    /**
     * ip:classSimpleName:methodName
     */
    IP,
    /**
     * args :classSimpleName:methodName
     */
    ARGS,
}
