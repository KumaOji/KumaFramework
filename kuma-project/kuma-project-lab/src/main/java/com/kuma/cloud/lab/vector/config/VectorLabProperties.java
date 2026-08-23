package com.kuma.cloud.lab.vector.config;

import com.kuma.boot.data.vector.core.model.Distance;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 向量库实验模块配置。
 */
@Data
@ConfigurationProperties(prefix = "kuma.lab.vector")
public class VectorLabProperties {

    /**
     * 实验用集合名，与 {@code kuma.boot.data.vector.collection} 保持一致。
     */
    private String collection = "lab-vectors";

    /**
     * 实验向量维度（小规模，便于手工构造与观察相似度）。
     */
    private int dimension = 4;

    /**
     * 相似度度量方式。
     */
    private Distance distance = Distance.COSINE;

}
