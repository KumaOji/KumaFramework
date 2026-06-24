package com.kuma.cloud.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 命令执行响应
 *
 * @author Kuma
 */
@Schema(description = "命令执行响应")
@Data
public class CommandVO {

    @Schema(description = "执行是否成功")
    private Boolean success;

    @Schema(description = "退出码")
    private Integer exitCode;

    @Schema(description = "标准输出")
    private String output;

    @Schema(description = "错误输出")
    private String error;

    @Schema(description = "执行时间（毫秒）")
    private Long executionTime;

    @Schema(description = "执行的完整命令")
    private String fullCommand;
}
