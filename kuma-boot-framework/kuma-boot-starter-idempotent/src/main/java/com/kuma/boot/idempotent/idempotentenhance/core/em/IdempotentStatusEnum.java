/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.em;

import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum IdempotentStatusEnum {
    PROCESSING(1),
    SUCCESS(2);

    private final int status;
    private static final Map<Integer, IdempotentStatusEnum> IDEMPOTENT_STATUS_ENUM_MAP;

    private IdempotentStatusEnum(int status) {
        this.status = status;
    }

    public static Optional<IdempotentStatusEnum> of(Integer status) {
        return Optional.ofNullable(IDEMPOTENT_STATUS_ENUM_MAP.get(status));
    }

    public static IdempotentStatusEnum getOrElseException(Integer status) {
        return IdempotentStatusEnum.of(status).orElseThrow(() -> new IdempotentException(String.format("can not map IdempotentStatus by [%s]", status)));
    }

    public int getStatus() {
        return this.status;
    }

    static {
        IDEMPOTENT_STATUS_ENUM_MAP = Arrays.stream(IdempotentStatusEnum.values()).collect(Collectors.toMap(IdempotentStatusEnum::getStatus, Function.identity()));
    }
}

