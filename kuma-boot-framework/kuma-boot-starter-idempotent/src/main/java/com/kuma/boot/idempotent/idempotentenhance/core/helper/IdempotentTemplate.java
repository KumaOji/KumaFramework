package com.kuma.boot.idempotent.idempotentenhance.core.helper;

import com.alibaba.fastjson2.JSON;
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
import com.kuma.boot.idempotent.idempotentenhance.core.registry.IdempotentRepositoryRegistry;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * <p>
 * 幂等助手, 需要在程序启动好后使用
 * </p>
 *
 * @author wenpan 2022/12/3 9:44 下午
 */
public class IdempotentTemplate {

    private final static String SOURCE = "IdempotentHelper";

    private final IdempotentExceptionEventHandler exceptionEventHandler;

    private final IdempotentCoreProperties idempotentCoreProperties;

    public IdempotentTemplate( @NonNull IdempotentExceptionEventHandler exceptionEventHandler,
                               @NonNull IdempotentCoreProperties idempotentCoreProperties ) {
        this.idempotentCoreProperties = idempotentCoreProperties;
        this.exceptionEventHandler = exceptionEventHandler;
        Preconditions.checkNotNull(exceptionEventHandler, "exceptionEventHandler can not be null.");
        Preconditions.checkNotNull(idempotentCoreProperties, "idempotentCoreProperties can not be null.");
    }

    protected IdempotentRepository lookupIdempotentRepository( String determineCurrentLookupKey ) {

        return IdempotentRepositoryRegistry.find(determineCurrentLookupKey);
    }

    public <R> R invoke( String source,
                         String operationType,
                         @NonNull String businessKey,
                         Type returnType,
                         Operation<R> operation ) throws Throwable {

        return invoke(source, operationType, businessKey, returnType, IdempotentRepositoryRegistry.PRIMARY, operation);
    }

    /**
     * <p>
     * 适配幂等注解的invoke方法, 因为注解切面调用该方法传入的operation并不是实际要执行的目标方法， 而是
     * {@link ProceedingJoinPoint#proceed()}。所以当存在幂等记录时，幂等返回上一次执行结果时需要将上一次 执行结果通过 JSON.parseObject 反序列化为对应的对象，此时由于并没有执行
     * operation.operation() 方法， 所以 JSON.parseObject 没有办法推断出实际要反序列的类结果类型，所以 JSON.parseObject 就会将结果反序列化为 JsonObject，
     * 这样便会导致类型强转换出错，所以需要外部传递方法的返回值类型
     * </p>
     *
     * @param source 来源
     * @param operationType 操作类型
     * @param businessKey 业务key
     * @param returnType 方法返回值类型
     * @param determineCurrentLookupKey 获取幂等repository的key
     * @param operation 函数式接口
     * @return R
     * @author wenpan 2023/1/7 12:10 下午
     */
    public <R> R invoke( String source,
                         String operationType,
                         @NonNull String businessKey,
                         Type returnType,
                         @NonNull String determineCurrentLookupKey,
                         Operation<R> operation ) throws Throwable {
        // 创建幂等实体
        IdempotentEntity entity = IdempotentEntity.create(
                businessKey, operationType, source, idempotentCoreProperties.getNamespace());

        // 未写入成功，说明已有幂等记录
        if (!lookupIdempotentRepository(determineCurrentLookupKey).create(entity)) {
            // 查询幂等记录，如果查询不到则抛出异常
            entity = lookupIdempotentRepository(determineCurrentLookupKey).query(entity)
                    .orElseThrow(() -> new IdempotentException(
                            String.format("can not found idempotent record businessKey is [%s].", businessKey)));
            // 获取上次执行的状态
            IdempotentStatusEnum statusEnum = IdempotentStatusEnum.getOrElseException(entity.getIdempotentStatus());
            // 上次处理已成功，幂等返回上次处理结果
            if (statusEnum == IdempotentStatusEnum.SUCCESS) {
                return JSON.parseObject(entity.getResponse(), returnType);
            }
            // 幂等状态为处理中，则尝试自愈，自愈成功便可以执行目标方法，反之则抛出异常中断
            trySelfCure(entity, determineCurrentLookupKey);
        }
        return invokeTargetAndReturn(operation, entity, determineCurrentLookupKey);
    }

    public <R> R invoke( String source,
                         String operationType,
                         @NonNull String businessKey,
                         Operation<R> operation ) throws Throwable {

        return invoke(source, operationType, businessKey, IdempotentRepositoryRegistry.PRIMARY, operation);
    }

    /**
     * 幂等执行invoke方法（带返回值）
     *
     * @param source 来源
     * @param operationType 操作类型
     * @param businessKey 业务key
     * @param operation 函数式接口
     * @return R
     * @author wenpan 2023/1/7 12:10 下午
     */
    public <R> R invoke( String source,
                         String operationType,
                         @NonNull String businessKey,
                         @NonNull String determineCurrentLookupKey,
                         Operation<R> operation ) throws Throwable {
        // 创建幂等实体
        IdempotentEntity entity = IdempotentEntity.create(
                businessKey, operationType, source, idempotentCoreProperties.getNamespace());
        boolean createSuccess = lookupIdempotentRepository(determineCurrentLookupKey).create(entity);

        // 未写入成功，说明已有幂等记录
        if (!createSuccess) {
            // 查询幂等记录，如果查询不到则抛出异常
            entity = lookupIdempotentRepository(determineCurrentLookupKey).query(entity)
                    .orElseThrow(() -> new IdempotentException(
                            String.format("can not found idempotent record businessKey is [%s].", businessKey)));
            // 获取上次执行的状态
            IdempotentStatusEnum statusEnum = IdempotentStatusEnum.getOrElseException(entity.getIdempotentStatus());
            // 上次处理已成功，幂等返回上次处理结果
            if (statusEnum == IdempotentStatusEnum.SUCCESS) {
                return JSON.parseObject(entity.getResponse(), new TypeReference<R>() {
                });
            }
            // 幂等状态为处理中，则尝试自愈
            trySelfCure(entity, determineCurrentLookupKey);
        }

        return invokeTargetAndReturn(operation, entity, determineCurrentLookupKey);
    }


    public void invokeWithNoResult( String source,
                                    String operationType,
                                    @NonNull String businessKey,
                                    OperationWithNoResult operation ) throws Throwable {
        invokeWithNoResult(source, operationType, businessKey, IdempotentRepositoryRegistry.PRIMARY, operation);
    }

    /**
     * 幂等执行invoke方法（不带返回值）
     *
     * @param source 来源
     * @param operationType 操作类型
     * @param businessKey 业务key
     * @param operation 函数式接口
     * @author wenpan 2023/1/7 12:10 下午
     */
    public void invokeWithNoResult( String source,
                                    String operationType,
                                    @NonNull String businessKey,
                                    @NonNull String determineCurrentLookupKey,
                                    OperationWithNoResult operation ) throws Throwable {
        // 创建幂等实体
        IdempotentEntity entity = IdempotentEntity.create(
                businessKey, operationType, source, idempotentCoreProperties.getNamespace());
        boolean createSuccess = lookupIdempotentRepository(determineCurrentLookupKey).create(entity);

        // 未写入成功，说明已有幂等记录
        if (!createSuccess) {
            // 查询幂等记录，如果查询不到则抛出异常
            entity = lookupIdempotentRepository(determineCurrentLookupKey).query(entity).orElseThrow(
                    () -> new IdempotentException(
                            String.format("can not found idempotent record businessKey is [%s].", businessKey)));
            // 获取上次执行的状态
            IdempotentStatusEnum statusEnum = IdempotentStatusEnum.getOrElseException(entity.getIdempotentStatus());
            // 上次处理已成功，幂等返回上次处理结果
            if (statusEnum == IdempotentStatusEnum.SUCCESS) {
                return;
            }
            // 幂等状态为处理中，则尝试自愈
            trySelfCure(entity, determineCurrentLookupKey);
        }

        try {
            // 执行业务逻辑
            operation.operation();
            targetInvokeSuccess(entity, determineCurrentLookupKey);
        } catch (Throwable ex) {
            // 业务执行失败，删除幂等记录（这里即使删除失败也不影响业务，因为PROCESSING状态的幂等记录超时会被重置）
            targetInvokeFailed(entity, determineCurrentLookupKey);
            throw ex;
        }
    }

    /**
     * 幂等记录存在并且幂等状态不为success时，尝试自愈，自愈逻辑如下：
     * <p>
     * <li>检查配置的 maxProcessTime 是否小于0 ，若小于0则说明不希望自愈，应该人工手动处理, 这种情况直接抛出异常</li>
     * <li>用上次处理时间 + 配置的 maxProcessTime 时间，和当前时间比较，如果大于当前时间则抛异常，反之则自愈</li>
     * </p>
     */
    private void trySelfCure( IdempotentEntity entity, String determineCurrentLookupKey ) {
        // 任务允许的最大处理时间
        long maxProcessTime = idempotentCoreProperties.getUnit()
                .toSeconds(idempotentCoreProperties.getMaxProcessTime());
        // 是否超过最大执行时间
        if (maxProcessTime <= IdempotentConstant.Digit.ZERO_LONG
                || entity.getLastModifiedDate().plusSeconds(maxProcessTime).compareTo(LocalDateTime.now()) > 0) {
            // 幂等并发异常
            throw new ConcurrentRequestException(
                    String.format("uniqueKey [%s] concurrent exception.", entity.getUniqueKey()));
        }
        // 自愈
        doSelfCure(entity, determineCurrentLookupKey);
    }

    /**
     * <p>
     * 自愈，也就是修改幂等记录的幂等状态为处理中，如果修改失败则抛出异常
     * </p>
     *
     * @param entity 待修改的实体
     */
    private void doSelfCure( @NonNull IdempotentEntity entity, String determineCurrentLookupKey ) {
        LogUtils.warn("{} process time exceed threshold, doSelfCure begin.", entity.getUniqueKey());
        // 设置修改日期为当前时间
        entity.setLastModifiedDate(LocalDateTime.now());
        // 幂等状态修改为处理中
        entity.setIdempotentStatus(IdempotentStatusEnum.PROCESSING.getStatus());
        // 更新幂等记录的幂等状态为处理中
        Boolean modify = lookupIdempotentRepository(determineCurrentLookupKey).changeIdempotentStatusProcessing(entity);
        LogUtils.warn("{} process time exceed threshold, doSelfCure result is {}.", entity.getUniqueKey(), modify);
        // 保证一定更新成功
        if (!Boolean.TRUE.equals(modify)) {
            throw new IdempotentException(
                    String.format("concurrent exception, modify [%s] to processing failed.", entity.getUniqueKey()));
        }
    }

    /**
     * <p>
     * 业务执行失败 这里catch掉删除异常，如果设置了 {@link IdempotentCoreProperties#getMaxProcessTime()}, 那么即使这里删除幂等记录失败了，等待 maxProcessTime
     * 时间后再次重试时便可以自愈
     * </p>
     */
    protected void targetInvokeFailed( @NonNull IdempotentEntity entity, String determineCurrentLookupKey ) {
        try {
            lookupIdempotentRepository(determineCurrentLookupKey).delete(entity);
        } catch (Exception e) {
            LogUtils.error("delete idempotent record failed, entity is {}.", entity, e);
            exceptionEventHandler.handle(new IdempotentExceptionEvent(entity.getUniqueKey(),
                    SOURCE, IdempotentExceptionEventTypeEnum.DELETE_RECORD_ERROR, entity));
        }
    }

    /**
     * 目标业务方法执行成功
     * <p>
     * 这里catch掉异常，也就是说即使在极端情况下没有将幂等记录更新成功也不能影响业务返回， 这样就可以最小程度的保证幂等的正确性
     * </p>
     */
    protected void targetInvokeSuccess( @NonNull IdempotentEntity entity, String determineCurrentLookupKey ) {
        boolean success = false;
        try {
            entity.setIdempotentStatus(IdempotentStatusEnum.SUCCESS.getStatus());
            // 设置修改日期为当前时间
            entity.setLastModifiedDate(LocalDateTime.now());
            // 更新幂等状态为成功时返回false，表示更新失败
            success = lookupIdempotentRepository(determineCurrentLookupKey).changeIdempotentStatusSuccess(entity);
        } catch (Exception ex) {
            // 更新幂等状态为成功时出现异常（极端情况）
            LogUtils.error(String.format("change the idempotent state to success failed, " +
                    "uniqueKey is [%s].", entity.getUniqueKey()), ex);
        }
        if (!success) {
            // catch异常
            try {
                LogUtils.error("change idempotentStatus to success failed, entity is {}", entity);
                exceptionEventHandler.handle(new IdempotentExceptionEvent(entity.getUniqueKey(),
                        SOURCE, IdempotentExceptionEventTypeEnum.CHANGE_STATUS_TO_SUCCESS_ERROR, entity));
            } catch (Exception ex) {
                LogUtils.error("handle IdempotentExceptionEvent failed, entity is {}", entity);
            }
        }
    }

    /**
     * <p>
     * 执行函数式接口并返回执行结果
     * </p>
     *
     * @param operation 目标方法
     * @param entity 幂等实体
     * @return R 目标方法返回值
     */
    @Nullable
    private <R> R invokeTargetAndReturn( Operation<R> operation,
                                         IdempotentEntity entity,
                                         String determineCurrentLookupKey ) throws Throwable {
        R operationResult;
        try {
            // 执行业务逻辑
            operationResult = operation.operation();
            if (operationResult != null) {
                entity.setResponse(JSON.toJSONString(operationResult));
            }
            targetInvokeSuccess(entity, determineCurrentLookupKey);
        } catch (Throwable ex) {
            // 业务执行失败，删除幂等记录（这里即使删除失败也不影响业务，因为 PROCESSING 状态的幂等记录超时会被自愈 或 人工处理）
            targetInvokeFailed(entity, determineCurrentLookupKey);
            throw ex;
        }
        return operationResult;
    }

}
