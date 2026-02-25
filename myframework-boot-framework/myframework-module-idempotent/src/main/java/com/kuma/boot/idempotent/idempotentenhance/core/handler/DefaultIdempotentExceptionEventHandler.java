/*
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 */
package com.kuma.boot.idempotent.idempotentenhance.core.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.event.IdempotentExceptionEvent;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class DefaultIdempotentExceptionEventHandler
implements IdempotentExceptionEventHandler,
ApplicationContextAware {
    private Executor executor;

    @Override
    public void handle(IdempotentExceptionEvent exceptionEvent) {
        this.executor.execute(() -> this.doHandle(exceptionEvent));
    }

    public void doHandle(IdempotentExceptionEvent event) {
        switch (event.getEventType()) {
            case DELETE_RECORD_ERROR: {
                this.doDeleteRecordError(event);
                break;
            }
            case CHANGE_STATUS_TO_SUCCESS_ERROR: {
                this.doChangeStatusToSuccessError(event);
                break;
            }
            default: {
                throw new IdempotentException("can not match eventType.");
            }
        }
    }

    public void doDeleteRecordError(IdempotentExceptionEvent event) {
        LogUtils.error((String)"========>>>>>>>> deleteRecordError event is {}", (Object[])new Object[]{event});
    }

    public void doChangeStatusToSuccessError(IdempotentExceptionEvent event) {
        LogUtils.error((String)"========>>>>>>>> changeStatusToSuccessError event is {}", (Object[])new Object[]{event});
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            this.executor = (Executor)applicationContext.getBean("idempotentExceptionEventExecutor", ExecutorService.class);
        }
        catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            // empty catch block
        }
        if (Objects.isNull(this.executor)) {
            LogUtils.warn((String)"======>>>>>> can not found idempotentExceptionEventExecutor, user default executor <<<<<<======", (Object[])new Object[0]);
            ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
            taskExecutor.setCorePoolSize(2);
            taskExecutor.setMaxPoolSize(5);
            taskExecutor.setThreadNamePrefix("defaultExceptionEventExecutor");
            taskExecutor.setKeepAliveSeconds(60);
            taskExecutor.setQueueCapacity(1024);
            taskExecutor.setAllowCoreThreadTimeOut(true);
            taskExecutor.setRejectedExecutionHandler((RejectedExecutionHandler)new ThreadPoolExecutor.AbortPolicy());
            taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
            taskExecutor.setAwaitTerminationSeconds(10);
            this.executor = taskExecutor;
        }
    }
}

