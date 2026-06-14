package com.kuma.boot.ai.model;

import lombok.Data;

@Data
public class RagIngestRequest {
    private String text;
    /** 来源标识（可选），用于后续按来源查看 / 删除；留空则归入占位来源 */
    private String source;
}
