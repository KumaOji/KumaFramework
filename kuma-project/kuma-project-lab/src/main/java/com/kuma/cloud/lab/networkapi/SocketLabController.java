package com.kuma.cloud.lab.networkapi;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.network.SocketLifecycleLesson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Socket 连接实验")
@RestController
@RequestMapping("/lab/socket")
public class SocketLabController {
    @Operation(summary = "Socket 完整实验：分帧、读取超时、UDP、长连接、半关闭与重连")
    @PostMapping("/scenario")
    public Result<SocketLifecycleLesson.Report> scenario() throws Exception { return Result.success(SocketLifecycleLesson.all()); }

    @Operation(summary = "Socket 生命周期：连续收发、连接内状态、半关闭、EOF、关闭后状态与重连")
    @PostMapping("/lifecycle")
    public Result<SocketLifecycleLesson.Report> lifecycle() throws Exception { return Result.success(SocketLifecycleLesson.run()); }
}
