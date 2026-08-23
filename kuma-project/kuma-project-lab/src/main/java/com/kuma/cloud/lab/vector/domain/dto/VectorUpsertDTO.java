package com.kuma.cloud.lab.vector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 向量写入参数。
 */
@Data
public class VectorUpsertDTO {

    /**
     * 记录 ID，为空时由向量库自动生成。
     */
    private String id;

    @NotBlank(message = "content 不能为空")
    private String content;

    /**
     * 显式向量；为空时根据 content 自动生成伪向量。
     */
    private List<Float> vector;

    /**
     * 业务元数据，可作为检索过滤条件。
     */
    private Map<String, Object> metadata;

}
