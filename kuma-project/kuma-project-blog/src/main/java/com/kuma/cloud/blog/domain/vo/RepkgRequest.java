package com.kuma.cloud.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "操作类型，默认 extract（提取）", example = "extract")
    private String operation = "extract";

    @Schema(description = "是否查找子文件夹（-c 参数）", example = "false")
    private Boolean findSubfolders = false;

    @Schema(description = "提取的条目类型（-e 参数），例如 tex", example = "tex")
    private String entryType;

    @Schema(description = "是否简化路径（-s 参数）", example = "false")
    private Boolean simplifyPaths = false;

    @Schema(description = "是否只转换 TEX 文件（-t 参数）", example = "false")
    private Boolean texOnly = false;

    @Schema(description = "超时时间（秒），默认 300 秒", example = "300")
    private Integer timeout = 300;

    @Schema(description = "工作目录（可选，覆盖默认的 RePKG.exe 所在目录）")
    private String workingDirectory;
}
