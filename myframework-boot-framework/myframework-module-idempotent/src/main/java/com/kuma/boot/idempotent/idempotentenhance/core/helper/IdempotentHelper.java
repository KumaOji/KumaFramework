/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSONObject
 *  com.alibaba.fastjson2.JSONReader$Feature
 *  com.alibaba.fastjson2.JSONWriter$Feature
 *  com.alibaba.fastjson2.TypeReference
 *  com.google.common.base.Preconditions
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.jspecify.annotations.NonNull
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.idempotent.idempotentenhance.core.helper;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.base.Preconditions;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.constants.IdempotentConstant;
import com.kuma.boot.idempotent.idempotentenhance.core.em.IdempotentExceptionEventTypeEnum;
import com.kuma.boot.idempotent.idempotentenhance.core.em.IdempotentStatusEnum;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.ConcurrentRequestException;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import com.kuma.boot.idempotent.idempotentenhance.core.function.Operation;
import com.kuma.boot.idempotent.idempotentenhance.core.function.OperationWithNoResult;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.IdempotentExceptionEventHandler;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.event.IdempotentExceptionEvent;
import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class IdempotentHelper {
    private static final String SOURCE = "IdempotentHelper";
    private final IdempotentRepository idempotentRepository;
    private IdempotentExceptionEventHandler exceptionEventHandler;
    private final IdempotentCoreProperties idempotentCoreProperties;

    public IdempotentHelper(@NonNull IdempotentExceptionEventHandler exceptionEventHandler, @NonNull IdempotentRepository idempotentRepository, @NonNull IdempotentCoreProperties idempotentCoreProperties) {
        this.idempotentRepository = idempotentRepository;
        this.idempotentCoreProperties = idempotentCoreProperties;
        this.exceptionEventHandler = exceptionEventHandler;
        Preconditions.checkNotNull((Object)idempotentRepository, (Object)"idempotentRepository can not be null.");
        Preconditions.checkNotNull((Object)exceptionEventHandler, (Object)"exceptionEventHandler can not be null.");
        Preconditions.checkNotNull((Object)idempotentCoreProperties, (Object)"idempotentCoreProperties can not be null.");
    }

    public <R> R invoke(String source, String operationType, @NonNull String businessKey, Type returnType, Operation<R> operation) throws Throwable {
        IdempotentEntity entity = IdempotentEntity.create(businessKey, operationType, source, this.idempotentCoreProperties.getNamespace());
        if (!this.idempotentRepository.create(entity)) {
            IdempotentStatusEnum statusEnum = IdempotentStatusEnum.getOrElseException((entity = this.idempotentRepository.query(entity).orElseThrow(() -> new IdempotentException(String.format("can not found idempotent record businessKey is [%s].", businessKey)))).getIdempotentStatus());
            if (statusEnum == IdempotentStatusEnum.SUCCESS) {
                return (R)JSONObject.parseObject((String)entity.getResponse(), (Type)returnType, (JSONReader.Feature[])new JSONReader.Feature[0]);
            }
            this.trySelfCure(entity);
        }
        return this.invokeTargetAndReturn(operation, entity);
    }

    public <R> R invoke(String source, String operationType, @NonNull String businessKey, Operation<R> operation) throws Throwable {
        IdempotentEntity entity = IdempotentEntity.create(businessKey, operationType, source, this.idempotentCoreProperties.getNamespace());
        boolean createSuccess = this.idempotentRepository.create(entity);
        if (!createSuccess) {
            IdempotentStatusEnum statusEnum = IdempotentStatusEnum.getOrElseException((entity = this.idempotentRepository.query(entity).orElseThrow(() -> new IdempotentException(String.format("can not found idempotent record businessKey is [%s].", businessKey)))).getIdempotentStatus());
            if (statusEnum == IdempotentStatusEnum.SUCCESS) {
                return (R)JSONObject.parseObject((String)entity.getResponse(), (TypeReference)new TypeReference<R>(this){
                    {
                        Objects.requireNonNull(this$0);
                    }
                }, (JSONReader.Feature[])new JSONReader.Feature[0]);
            }
            this.trySelfCure(entity);
        }
        return this.invokeTargetAndReturn(operation, entity);
    }

    public void invokeWithNoResult(String source, String operationType, @NonNull String businessKey, OperationWithNoResult operation) throws Throwable {
        IdempotentEntity entity = IdempotentEntity.create(businessKey, operationType, source, this.idempotentCoreProperties.getNamespace());
        boolean createSuccess = this.idempotentRepository.create(entity);
        if (!createSuccess) {
            IdempotentStatusEnum statusEnum = IdempotentStatusEnum.getOrElseException((entity = this.idempotentRepository.query(entity).orElseThrow(() -> new IdempotentException(String.format("can not found idempotent record businessKey is [%s].", businessKey)))).getIdempotentStatus());
            if (statusEnum == IdempotentStatusEnum.SUCCESS) {
                return;
            }
            this.trySelfCure(entity);
        }
        try {
            operation.operation();
            this.targetInvokeSuccess(entity);
        }
        catch (Throwable ex) {
            this.targetInvokeFailed(entity);
            throw ex;
        }
    }

    private void trySelfCure(IdempotentEntity entity) {
        long maxProcessTime = this.idempotentCoreProperties.getUnit().toSeconds(this.idempotentCoreProperties.getMaxProcessTime());
        if (maxProcessTime <= IdempotentConstant.Digit.ZERO_LONG || entity.getLastModifiedDate().plusSeconds(maxProcessTime).compareTo(LocalDateTime.now()) > 0) {
            throw new ConcurrentRequestException(String.format("uniqueKey [%s] concurrent exception.", entity.getUniqueKey()));
        }
        this.doSelfCure(entity);
    }

    private void doSelfCure(@NonNull IdempotentEntity entity) {
        LogUtils.warn((String)"{} process time exceed threshold, doSelfCure begin.", (Object[])new Object[]{entity.getUniqueKey()});
        entity.setLastModifiedDate(LocalDateTime.now());
        entity.setIdempotentStatus(IdempotentStatusEnum.PROCESSING.getStatus());
        Boolean modify = this.idempotentRepository.changeIdempotentStatusProcessing(entity);
        LogUtils.warn((String)"{} process time exceed threshold, doSelfCure result is {}.", (Object[])new Object[]{entity.getUniqueKey(), modify});
        if (!Boolean.TRUE.equals(modify)) {
            throw new IdempotentException(String.format("concurrent exception, modify [%s] to processing failed.", entity.getUniqueKey()));
        }
    }

    protected void targetInvokeFailed(@NonNull IdempotentEntity entity) {
        try {
            this.idempotentRepository.delete(entity);
        }
        catch (Exception e) {
            LogUtils.error((String)"delete idempotent record failed, entity is {}.", (Object[])new Object[]{entity, e});
            this.exceptionEventHandler.handle(new IdempotentExceptionEvent(entity.getUniqueKey(), SOURCE, IdempotentExceptionEventTypeEnum.DELETE_RECORD_ERROR, entity));
        }
    }

    protected void targetInvokeSuccess(@NonNull IdempotentEntity entity) {
        boolean success = false;
        try {
            entity.setIdempotentStatus(IdempotentStatusEnum.SUCCESS.getStatus());
            entity.setLastModifiedDate(LocalDateTime.now());
            success = this.idempotentRepository.changeIdempotentStatusSuccess(entity);
        }
        catch (Exception ex) {
            LogUtils.error((String)String.format("change the idempotent state to success failed, uniqueKey is [%s].", entity.getUniqueKey()), (Object[])new Object[]{ex});
        }
        if (!success) {
            try {
                LogUtils.error((String)"change idempotentStatus to success failed, entity is {}", (Object[])new Object[]{entity});
                this.exceptionEventHandler.handle(new IdempotentExceptionEvent(entity.getUniqueKey(), SOURCE, IdempotentExceptionEventTypeEnum.CHANGE_STATUS_TO_SUCCESS_ERROR, entity));
            }
            catch (Exception ex) {
                LogUtils.error((String)"handle IdempotentExceptionEvent failed, entity is {}", (Object[])new Object[]{entity});
            }
        }
    }

    private <R> @Nullable R invokeTargetAndReturn(Operation<R> operation, IdempotentEntity entity) throws Throwable {
        R operationResult;
        try {
            operationResult = operation.operation();
            if (operationResult != null) {
                entity.setResponse(JSONObject.toJSONString(operationResult, (JSONWriter.Feature[])new JSONWriter.Feature[0]));
            }
            this.targetInvokeSuccess(entity);
        }
        catch (Throwable ex) {
            this.targetInvokeFailed(entity);
            throw ex;
        }
        return operationResult;
    }
}

