package com.kuma.cloud.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * RePKG 工具请求参数
 *
 * @author Kuma
 */
@Schema(description = "RePKG 工具请求参数")
@Data
public class RepkgRequest {

    @Schema(description = "repkg.exe 的路径，为空时使用配置的默认路径")
    private String repkgPath;

    @Schema(description = "操作类型：extract 或 list，默认 extract", example = "extract")
    @Pattern(regexp = "^(extract|list)$", message = "operation 只允许 extract 或 list")
    private String operation = "extract";

    @Schema(description = "是否查找子文件夹（-c 参数）", example = "false")
    private Boolean findSubfolders = false;

    @Schema(description = "提取的条目类型（-e 参数），只允许字母数字下划线，例如 tex", example = "tex")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{1,32}$", message = "entryType 只允许字母、数字、下划线、连字符，最长 32 位")
    private String entryType;

    @Schema(description = "是否简化路径（-s 参数）", example = "false")
    private Boolean simplifyPaths = false;

    @Schema(description = "是否只转换 TEX 文件（-t 参数）", example = "false")
    private Boolean texOnly = false;

    @Schema(description = "超时时间（秒），1~600，默认 300 秒", example = "300")
    @Min(value = 1, message = "timeout 最小为 1 秒")
    @Max(value = 600, message = "timeout 最大为 600 秒")
    private Integer timeout = 300;
}
