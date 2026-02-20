package com.kuma.boot.common.enums;

public enum StatusEnum {
    SUCCESS("成功"),
    FAILURE("失败"),
    PROCESSING("处理中"),
    UNKNOWN("状态未知或幂等冲突或重试或重复请求或未知异常"),
    ;

    StatusEnum(String description) {
        this.description = description;
    }

    private final String description;

    public String getDescription() {
        return description;
    }
}
