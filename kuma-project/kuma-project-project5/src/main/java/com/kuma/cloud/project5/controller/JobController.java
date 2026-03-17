package com.kuma.cloud.project5.controller;

import com.kuma.cloud.job.client.producer.KmcJobTemplate;
import com.kuma.cloud.job.client.producer.entity.JobUpdateReq;
import com.kuma.cloud.job.common.enums.TimeExpressionType;
import com.kuma.cloud.job.common.module.LifeCycle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务创建接口（演示用）
 * <p>
 * 通过 HTTP 调用，向 job-server 提交一个定时任务，由 job-server 调度后
 * 下发给 project5 Worker 执行对应的处理器。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {

    @Value("${kmcjob.worker.name-server-address}")
    private String nameServerAddress;

    /**
     * 创建一个 Cron 任务
     *
     * @param processorInfo Bean 名称或 @KmcJobHandler name，如 simpleJobProcessor / sendNoticeJob
     * @param cron          Cron 表达式，如 * /30 * * * * ?（每30秒）
     * @param jobParams     传给处理器的参数
     * @return 新建任务的 jobId
     *
     * 示例：GET /job/create?processorInfo=simpleJobProcessor&cron=0/30 * * * * ?&jobParams=hello
     */
    @GetMapping("/create")
    public String create(
            @RequestParam(defaultValue = "simpleJobProcessor") String processorInfo,
            @RequestParam(defaultValue = "0/30 * * * * ?") String cron,
            @RequestParam(defaultValue = "test") String jobParams) {

        KmcJobTemplate template = new KmcJobTemplate(nameServerAddress);

        JobUpdateReq req = JobUpdateReq.builder()
                .appName("project5")
                .jobName(processorInfo + "-job")
                .jobDescription("project5 演示任务：" + processorInfo)
                .jobParams(jobParams)
                .processorInfo(processorInfo)
                .timeExpressionType(TimeExpressionType.CRON)
                .timeExpression(cron)
                .lifeCycle(new LifeCycle())
                .maxInstanceNum(1)
                .build();

        Long jobId = template.createJob(req);
        log.info("[JobController] 任务创建成功，processorInfo={}, cron={}, jobId={}", processorInfo, cron, jobId);
        return "jobId=" + jobId;
    }
}
