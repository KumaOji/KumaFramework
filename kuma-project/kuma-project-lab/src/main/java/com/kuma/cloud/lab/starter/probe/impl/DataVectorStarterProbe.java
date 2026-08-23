package com.kuma.cloud.lab.starter.probe.impl;

import com.kuma.boot.data.vector.core.VectorStore;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.cloud.lab.starter.config.StarterLabProperties;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeStepVO;
import com.kuma.cloud.lab.starter.probe.AbstractStarterProbe;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataVectorStarterProbe extends AbstractStarterProbe {

    private final StarterLabProperties starterLabProperties;

    public DataVectorStarterProbe(StarterLabProperties starterLabProperties) {
        super(
                StarterNameConstants.DATA_VECTOR_STARTER,
                "com.kuma.boot.data.vector.autoconfigure.VectorStoreAutoConfiguration",
                "向量存储抽象与相似度检索"
        );
        this.starterLabProperties = starterLabProperties;
    }

    @Override
    protected StarterProbeResultVO doDiagnose(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        boolean vectorStoreReady = hasBean(applicationContext, VectorStore.class);
        steps.add(step("bean.vectorStore", vectorStoreReady,
                vectorStoreReady ? "VectorStore Bean 已注册" : "VectorStore Bean 缺失"));
        if (!vectorStoreReady) {
            return result(StarterProbeStatus.FAILED, "Vector Starter Bean 未就绪", steps, detailsOf());
        }
        VectorStore vectorStore = requireBean(applicationContext, VectorStore.class, "VectorStore");
        String provider = vectorStore.provider();
        steps.add(step("vector.provider", true, "provider=" + provider));
        return result(
                StarterProbeStatus.READY,
                "Vector Starter 已就绪",
                steps,
                detailsOf("provider", provider)
        );
    }

    @Override
    protected StarterProbeResultVO doSmokeTest(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        if (!hasBean(applicationContext, VectorStore.class)) {
            steps.add(step("vector.count", false, "VectorStore 不可用"));
            return result(StarterProbeStatus.SKIPPED, "向量库未配置，跳过冒烟测试", steps, detailsOf());
        }
        try {
            VectorStore vectorStore = requireBean(applicationContext, VectorStore.class, "VectorStore");
            String collection = starterLabProperties.getKeyPrefix() + "probe";
            long before = vectorStore.count(collection);
            long after = vectorStore.count(collection);
            boolean success = before >= 0 && after >= 0;
            steps.add(step("vector.count", success, "count before=" + before + ", after=" + after));
            return result(
                    success ? StarterProbeStatus.PASSED : StarterProbeStatus.FAILED,
                    success ? "Vector Starter 冒烟测试通过" : "Vector Starter 冒烟测试失败",
                    steps,
                    detailsOf("collection", collection, "provider", vectorStore.provider())
            );
        } catch (Exception error) {
            steps.add(step("vector.count", false, error.getMessage()));
            return result(
                    StarterProbeStatus.FAILED,
                    "Vector Starter 冒烟测试失败: " + error.getMessage(),
                    steps,
                    detailsOf()
            );
        }
    }

}
