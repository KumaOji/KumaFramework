package com.kuma.cloud.lab.elasticsearch;

import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Elasticsearch 实验")
@RestController
@RequestMapping("/lab/elasticsearch")
public class ElasticsearchLabController {
    @Operation(summary = "ES 学习目录：索引与 CRUD、全文搜索、分组聚合")
    @GetMapping("/topics")
    public Result<List<ElasticsearchLearningDemo.Topic>> topics() {
        return Result.success(ElasticsearchLearningDemo.topics());
    }

    @Operation(summary = "ES 完整实验：Mapping、refresh、CRUD、全文搜索与聚合，自动清理独立索引")
    @PostMapping("/scenario")
    public Result<ElasticsearchLearningDemo.Report> scenario() throws Exception { return run("all"); }

    @Operation(summary = "ES CRUD：创建索引、批量写入、实时读取、refresh、部分更新与删除")
    @PostMapping("/crud")
    public Result<ElasticsearchLearningDemo.Report> crud() throws Exception { return run("crud"); }

    @Operation(summary = "ES 搜索：分词、match/term 对照、bool/filter/range、排序与高亮")
    @PostMapping("/search")
    public Result<ElasticsearchLearningDemo.Report> search() throws Exception { return run("search"); }

    @Operation(summary = "ES 聚合：keyword 分桶、每组均价与全局价格统计")
    @PostMapping("/aggregation")
    public Result<ElasticsearchLearningDemo.Report> aggregation() throws Exception { return run("aggregation"); }

    private Result<ElasticsearchLearningDemo.Report> run(String lesson) throws Exception {
        try (var lab = new ElasticsearchLearningDemo(ElasticsearchLearningDemo.configuredEndpoint())) {
            return Result.success(lab.run(lesson));
        }
    }
}
