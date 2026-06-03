package com.kuma.boot.idempotent.idempotentenhance.core.em;


/**
 * 幂等异常事件类型枚举
 *
 * @author wenpan 2023/01/07 13:11
 */
public enum IdempotentExceptionEventTypeEnum {

    /**
     * 删除幂等记录失败
     */
    DELETE_RECORD_ERROR("deleteRecordError", "删除幂等记录失败"),
    /**
     * 修改幂等状态为success失败
     */
    CHANGE_STATUS_TO_SUCCESS_ERROR("changeStatusToSuccessError", "修改幂等状态为success失败");

    IdempotentExceptionEventTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 类型
     */
    private final String type;
    /**
     * 类型描述
     */
    private final String desc;

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
