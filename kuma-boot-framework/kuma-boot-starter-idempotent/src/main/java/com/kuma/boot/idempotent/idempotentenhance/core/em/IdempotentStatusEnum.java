package com.kuma.boot.idempotent.idempotentenhance.core.em;

import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 幂等状态枚举
 *
 * @author wenpan 2022/12/31 15:20
 */
public enum IdempotentStatusEnum {

    /**
     * 执行中
     */
    PROCESSING(1),

    SUCCESS(2);

    private final int status;

    IdempotentStatusEnum(int status) {
        this.status = status;
    }

    private static final Map<Integer, IdempotentStatusEnum> IDEMPOTENT_STATUS_ENUM_MAP = Arrays.stream(IdempotentStatusEnum.values())
            .collect(Collectors.toMap(IdempotentStatusEnum::getStatus, Function.identity()));

    /**
     * 通过status映射枚举
     */
    public static Optional<IdempotentStatusEnum> of(Integer status) {

        return Optional.ofNullable(IDEMPOTENT_STATUS_ENUM_MAP.get(status));
    }

    /**
     * 通过status映射枚举类，如果映射不上则抛出异常
     *
     * @param status 状态
     * @return org.enhance.idempotent.core.em.IdempotentStatusEnum
     * @author wenpan 2023/1/2 3:21 下午
     */
    public static IdempotentStatusEnum getOrElseException(Integer status) {

        return of(status).orElseThrow(() -> new IdempotentException(String.format("can not map IdempotentStatus by [%s]", status)));
    }

    public int getStatus() {
        return status;
    }
}
