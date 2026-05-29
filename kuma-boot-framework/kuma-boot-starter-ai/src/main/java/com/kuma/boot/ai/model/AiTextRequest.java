package com.kuma.boot.ai.model;

import lombok.Data;

@Data
public class AiTextRequest {

    private String text;
    private String model;
    /** 目标语言，translate 时使用，例如 "中文"、"English" */
    private String language;
    /** 最大字数，summarize 时使用，默认 200 */
    private Integer maxWords;
    /** 关键词数量，extractKeywords 时使用，默认 5 */
    private Integer count;
}
