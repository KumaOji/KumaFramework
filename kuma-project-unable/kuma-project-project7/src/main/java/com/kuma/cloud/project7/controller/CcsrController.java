package com.kuma.cloud.project7.controller;

import com.kuma.cloud.ccsr.api.event.EventType;
import com.kuma.cloud.ccsr.api.grpc.auto.Response;
import com.kuma.cloud.ccsr.client.request.Payload;
import com.kuma.cloud.ccsr.client.starter.CcsrService;
import com.kuma.cloud.project7.dto.TextConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CCSR 配置读写示例接口
 */
@RestController
@RequestMapping("/ccsr")
@RequiredArgsConstructor
public class CcsrController {

    private final CcsrService ccsrService;

    /**
     * 写入配置
     *
     * @param group   配置分组，默认 default_group
     * @param dataId  配置 ID，默认 default_data_id
     * @param content 配置内容
     */
    @PutMapping("/config")
    public String putConfig(
            @RequestParam(defaultValue = "default_group") String group,
            @RequestParam(defaultValue = "default_data_id") String dataId,
            @RequestParam String content) {
        Payload payload = Payload.builder()
                .group(group)
                .dataId(dataId)
                .configData(new TextConfig(content))
                .build();
        Response response = ccsrService.request(payload, EventType.PUT);
        return response.toString();
    }

    /**
     * 读取配置
     *
     * @param group  配置分组，默认 default_group
     * @param dataId 配置 ID，默认 default_data_id
     */
    @GetMapping("/config")
    public String getConfig(
            @RequestParam(defaultValue = "default_group") String group,
            @RequestParam(defaultValue = "default_data_id") String dataId) {
        Payload payload = Payload.builder()
                .group(group)
                .dataId(dataId)
                .build();
        Response response = ccsrService.request(payload, EventType.GET);
        return response.toString();
    }

    /**
     * 删除配置
     *
     * @param group  配置分组，默认 default_group
     * @param dataId 配置 ID，默认 default_data_id
     */
    @GetMapping("/config/delete")
    public String deleteConfig(
            @RequestParam(defaultValue = "default_group") String group,
            @RequestParam(defaultValue = "default_data_id") String dataId) {
        Payload payload = Payload.builder()
                .group(group)
                .dataId(dataId)
                .build();
        Response response = ccsrService.request(payload, EventType.DELETE);
        return response.toString();
    }
}
