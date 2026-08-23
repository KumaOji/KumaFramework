package com.kuma.cloud.lab.vector.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.vector.domain.dto.VectorSearchDTO;
import com.kuma.cloud.lab.vector.domain.dto.VectorUpsertDTO;
import com.kuma.cloud.lab.vector.domain.vo.VectorDocumentVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorMatchVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorScenarioVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorStoreStatusVO;
import com.kuma.cloud.lab.vector.service.VectorTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "向量数据库测试")
@RestController
@RequestMapping("/lab/vector")
@RequiredArgsConstructor
public class VectorTestController {

    private final VectorTestService vectorTestService;

    @Operation(summary = "执行向量库场景测试（写入 / 检索 / 过滤 / 删除）")
    @PostMapping("/scenario")
    public Result<VectorScenarioVO> scenario() {
        return Result.success(vectorTestService.runScenario());
    }

    @Operation(summary = "写入或覆盖一条向量记录")
    @PostMapping("/upsert")
    public Result<VectorDocumentVO> upsert(@Valid @RequestBody VectorUpsertDTO dto) {
        return Result.success(vectorTestService.upsert(dto));
    }

    @Operation(summary = "向量相似度检索")
    @PostMapping("/search")
    public Result<List<VectorMatchVO>> search(@Valid @RequestBody VectorSearchDTO dto) {
        return Result.success(vectorTestService.search(dto));
    }

    @Operation(summary = "按 ID 删除向量记录")
    @DeleteMapping("/{id}")
    public Result<Long> delete(@PathVariable String id) {
        return Result.success(vectorTestService.delete(id));
    }

    @Operation(summary = "重置实验集合（删除后重建）")
    @DeleteMapping("/collection")
    public Result<Long> resetCollection() {
        return Result.success(vectorTestService.resetCollection());
    }

    @Operation(summary = "获取向量库状态")
    @GetMapping("/status")
    public Result<VectorStoreStatusVO> status() {
        return Result.success(vectorTestService.status());
    }

}
