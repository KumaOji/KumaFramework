package com.kuma.boot.idempotent.idempotentenhance.core.pojo;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.constants.IdempotentConstant;
import com.kuma.boot.idempotent.idempotentenhance.core.em.IdempotentStatusEnum;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 幂等实体
 *
 * @author wenpan 2022/12/31 15:25
 */
public class IdempotentEntity implements Serializable {

    /**
     * 命名空间（用于应用隔离）
     */
    private String namespace;

    /**
     * 来源
     */
    private String source;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 业务号（一般为用于做幂等的业务code）
     */
    private String businessKey;

    /**
     * 幂等状态
     */
    private Integer idempotentStatus;

    /**
     * 请求响应
     */
    private String response;

    /**
     * 版本号
     */
    private Long objectVersionNumber;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 最近修改时间
     */
    private LocalDateTime lastModifiedDate;

    /**
     * 唯一key
     */
    private String uniqueKey;

    public IdempotentEntity() {
    }

    public static IdempotentEntity create(String businessKey,
                                          String operationType,
                                          String source,
                                          String namespace) {
        Preconditions.checkArgument(StringUtils.isNotBlank(businessKey), "businessKey can not be null.");
        IdempotentEntity entity = new IdempotentEntity();
        entity.setBusinessKey(businessKey);
        entity.setOperationType(operationType);
        entity.setSource(source);
        entity.setNamespace(namespace);
        entity.setIdempotentStatus(IdempotentStatusEnum.PROCESSING.getStatus());
        entity.setObjectVersionNumber(IdempotentConstant.Digit.ONE_LONG);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateDate(now);
        entity.setLastModifiedDate(now);
        entity.setUniqueKey(buildUniqueKey(businessKey, operationType, source, namespace));
        return entity;
    }

    /**
     * <p>
     * 构建唯一键，由四元组拼接而来
     * </p>
     *
     * @return java.lang.String 唯一键
     * @author wenpan 2023/1/3 9:35 下午
     */
    public static String buildUniqueKey(String businessKey, String operationType, String source, String namespace) {
        if (StringUtils.isBlank(businessKey)) {
            throw new IdempotentException("can not build unique key, because businessKey is null.");
        }
        StringBuilder builder = new StringBuilder(businessKey);
        if (StringUtils.isNotBlank(operationType)) {
            builder.append(IdempotentConstant.Symbol.UNDERLINE).append(operationType);
        }
        if (StringUtils.isNotBlank(source)) {
            builder.append(IdempotentConstant.Symbol.UNDERLINE).append(source);
        }
        if (StringUtils.isNotBlank(namespace)) {
            builder.append(IdempotentConstant.Symbol.UNDERLINE).append(namespace);
        }
        return builder.toString();
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace( String namespace ) {
        this.namespace = namespace;
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType( String operationType ) {
        this.operationType = operationType;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey( String businessKey ) {
        this.businessKey = businessKey;
    }

    public Integer getIdempotentStatus() {
        return idempotentStatus;
    }

    public void setIdempotentStatus( Integer idempotentStatus ) {
        this.idempotentStatus = idempotentStatus;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse( String response ) {
        this.response = response;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber( Long objectVersionNumber ) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate( LocalDateTime createDate ) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate( LocalDateTime lastModifiedDate ) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey( String uniqueKey ) {
        this.uniqueKey = uniqueKey;
    }
}
