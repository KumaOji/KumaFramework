package com.kuma.cloud.project7.runner;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.ccsr.api.event.EventType;
import com.kuma.cloud.ccsr.api.grpc.auto.Response;
import com.kuma.cloud.ccsr.client.request.Payload;
import com.kuma.cloud.ccsr.client.starter.CcsrService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动后验证 CCSR 服务端连通性，在日志中明确打印连接结果
 */
@Component
@RequiredArgsConstructor
public class CcsrConnectionVerifier implements ApplicationRunner {

    private static final String PROBE_GROUP  = "probe_group";
    private static final String PROBE_DATA_ID = "probe_data_id";

    private final CcsrService ccsrService;

    @Override
    public void run(ApplicationArguments args) {
        LogUtils.info("[CCSR] 开始验证与 CCSR 服务端的连通性...");
        try {
            Payload payload = Payload.builder()
                    .group(PROBE_GROUP)
                    .dataId(PROBE_DATA_ID)
                    .build();
            Response response = ccsrService.request(payload, EventType.GET);

            if (response.getSuccess()) {
                LogUtils.info("[CCSR] 连接验证成功 ✓  code={}, msg={}", response.getCode(), response.getMsg());
            } else {
                LogUtils.warn("[CCSR] 连接验证失败 ✗  code={}, msg={}", response.getCode(), response.getMsg());
            }
        } catch (Exception e) {
            LogUtils.error("[CCSR] 连接验证异常，服务端可能未启动或地址配置有误: {}", e.getMessage());
        }
    }
}
