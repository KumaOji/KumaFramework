package com.kuma.cloud.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传响应
 *
 * @author Kuma
 */
@Schema(description = "文件上传响应")
@Data
public class FileUploadResponse {

    @Schema(description = "原始文件名")
    private String originalFilename;

    @Schema(description = "保存后的文件名")
    private String savedFilename;

    @Schema(description = "文件保存路径")
    private String filePath;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String contentType;
}
