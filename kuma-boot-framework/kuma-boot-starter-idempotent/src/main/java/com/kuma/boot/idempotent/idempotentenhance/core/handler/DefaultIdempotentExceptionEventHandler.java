package com.kuma.boot.idempotent.idempotentenhance.core.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.constants.IdempotentConstant;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.event.IdempotentExceptionEvent;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 默认幂等异常事件处理器, 将事件提交到线程池执行
 * </p>
 *
 * @author wenpan 2023/01/07 15:18
 */
public class DefaultIdempotentExceptionEventHandler implements IdempotentExceptionEventHandler, ApplicationContextAware {

    private Executor executor;

    @Override
    public void handle(IdempotentExceptionEvent exceptionEvent) {
        // 提交到线程池内异步执行
        executor.execute(() -> doHandle(exceptionEvent));
    }

    public void doHandle(IdempotentExceptionEvent event) {
        switch (event.getEventType()) {
            case DELETE_RECORD_ERROR:
                doDeleteRecordError(event);
                break;
            case CHANGE_STATUS_TO_SUCCESS_ERROR:
                doChangeStatusToSuccessError(event);
                break;
            default:
                throw new IdempotentException("can not match eventType.");
        }
    }

    /**
     * 处理删除幂等记录失败
     *
     * @param event 事件
     * @author wenpan 2023/1/7 6:46 下午
     */
    public void doDeleteRecordError(IdempotentExceptionEvent event) {
        LogUtils.error("========>>>>>>>> deleteRecordError event is {}", event);
    }

    /**
     * 处理修改幂等状态为成功出现的异常
     *
     * @param event 事件
     * @author wenpan 2023/1/7 6:47 下午
     */
    public void doChangeStatusToSuccessError(IdempotentExceptionEvent event) {
        LogUtils.error("========>>>>>>>> changeStatusToSuccessError event is {}", event);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        try {
            executor = applicationContext.getBean(
                    IdempotentConstant.Executor.IDEMPOTENT_EXCEPTION_EVENT_EXECUTOR, ExecutorService.class);
        } catch (NoSuchBeanDefinitionException ex) {
            // do nothing
        }
        // 使用默认线程池
        if (Objects.isNull(executor)) {
            LogUtils.warn("======>>>>>> can not found idempotentExceptionEventExecutor, user default executor <<<<<<======");
            ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
            taskExecutor.setCorePoolSize(2);
            taskExecutor.setMaxPoolSize(5);
            taskExecutor.setThreadNamePrefix("defaultExceptionEventExecutor");
            taskExecutor.setKeepAliveSeconds(60);
            taskExecutor.setQueueCapacity(1024);
            taskExecutor.setAllowCoreThreadTimeOut(true);
            taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
            // 关机时如果线程池任务没有处理完，则预留10s给线程池处理剩余任务
            taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
            taskExecutor.setAwaitTerminationSeconds(10);
            executor = taskExecutor;
        }
    }
}
