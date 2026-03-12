package com.kuma.cloud.project5.job;

import com.kuma.cloud.job.worker.processor.ProcessResult;
import com.kuma.cloud.job.worker.processor.task.TaskContext;
import com.kuma.cloud.job.worker.processor.type.BasicProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 单机处理器示例（类型：BasicProcessor）
 * <p>
 * 在 job-server 控制台配置任务时，processorInfo 填写 Bean 名称：simpleJobProcessor
 * </p>
 */
@Slf4j
@Component
public class SimpleJobProcessor implements BasicProcessor {

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        String jobParams = context.getJobParams();
        log.info("[SimpleJobProcessor] 开始执行，jobParams={}", jobParams);

        // 模拟业务逻辑
        log.info("[SimpleJobProcessor] 处理参数: {}", jobParams);

        log.info("[SimpleJobProcessor] 执行完成");
        return new ProcessResult(true, "success, params=" + jobParams);
    }
}
