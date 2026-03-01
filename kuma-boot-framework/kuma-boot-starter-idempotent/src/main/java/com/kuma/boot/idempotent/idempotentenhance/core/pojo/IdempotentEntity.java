/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.kuma.boot.common.utils.lang.StringUtils
 */
package com.kuma.boot.idempotent.idempotentenhance.core.pojo;

import com.google.common.base.Preconditions;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.constants.IdempotentConstant;
import com.kuma.boot.idempotent.idempotentenhance.core.em.IdempotentStatusEnum;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import java.io.Serializable;
import java.time.LocalDateTime;

public class IdempotentEntity
implements Serializable {
    private String namespace;
    private String source;
    private String operationType;
    private String businessKey;
    private Integer idempotentStatus;
    private String response;
    private Long objectVersionNumber;
    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;
    private String uniqueKey;

    public static IdempotentEntity create(String businessKey, String operationType, String source, String namespace) {
        Preconditions.checkArgument((boolean)StringUtils.isNotBlank((String)businessKey), (Object)"businessKey can not be null.");
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
        entity.setUniqueKey(IdempotentEntity.buildUniqueKey(businessKey, operationType, source, namespace));
        return entity;
    }

    public static String buildUniqueKey(String businessKey, String operationType, String source, String namespace) {
        if (StringUtils.isBlank((String)businessKey)) {
            throw new IdempotentException("can not build unique key, because businessKey is null.");
        }
        StringBuilder builder = new StringBuilder(businessKey);
        if (StringUtils.isNotBlank((String)operationType)) {
            builder.append("_").append(operationType);
        }
        if (StringUtils.isNotBlank((String)source)) {
            builder.append("_").append(source);
        }
        if (StringUtils.isNotBlank((String)namespace)) {
            builder.append("_").append(namespace);
        }
        return builder.toString();
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOperationType() {
        return this.operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getBusinessKey() {
        return this.businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Integer getIdempotentStatus() {
        return this.idempotentStatus;
    }

    public void setIdempotentStatus(Integer idempotentStatus) {
        this.idempotentStatus = idempotentStatus;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Long getObjectVersionNumber() {
        return this.objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public LocalDateTime getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getUniqueKey() {
        return this.uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}

