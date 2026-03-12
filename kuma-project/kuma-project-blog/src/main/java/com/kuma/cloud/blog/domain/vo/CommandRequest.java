package com.kuma.cloud.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 命令执行请求
 *
 * @author Kuma
 */
@Schema(description = "命令执行请求")
@Data
public class CommandRequest {

    @Schema(description = "命令或可执行文件路径")
    private String command;

    @Schema(description = "命令参数列表")
    private List<String> args;

    @Schema(description = "工作目录（可选）")
    private String workingDirectory;

    @Schema(description = "超时时间（秒），默认 30 秒")
    private Integer timeout = 30;

    @Schema(description = "是否等待命令执行完成，默认 true")
    private Boolean waitForCompletion = true;
}
