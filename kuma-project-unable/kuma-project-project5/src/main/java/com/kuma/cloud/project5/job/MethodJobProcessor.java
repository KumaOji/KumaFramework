package com.kuma.cloud.project5.job;

import com.kuma.cloud.job.worker.annotation.KmcJobHandler;
import com.kuma.cloud.job.worker.processor.task.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 方法级处理器示例（使用 @KmcJobHandler 注解）
 * <p>
 * 在 job-server 控制台配置任务时，processorInfo 填写注解 name 值：
 *   - sendNoticeJob
 *   - cleanCacheJob
 * </p>
 */
@Slf4j
@Component
public class MethodJobProcessor {

    /**
     * 发送通知任务
     * processorInfo = sendNoticeJob
     */
    @KmcJobHandler(name = "sendNoticeJob")
    public void sendNotice(TaskContext context) {
        log.info("[sendNoticeJob] 发送通知，params={}", context.getJobParams());
        // 业务逻辑：发送短信/邮件/推送
    }

    /**
     * 清理缓存任务
     * processorInfo = cleanCacheJob
     */
    @KmcJobHandler(name = "cleanCacheJob")
    public void cleanCache(TaskContext context) {
        log.info("[cleanCacheJob] 清理缓存，params={}", context.getJobParams());
        // 业务逻辑：清理过期缓存
    }
}
