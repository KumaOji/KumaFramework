package com.kuma.cloud.lab.elasticsearch;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Uses ES REST directly so the report exposes the actual Query DSL, without starting Spring. */
public final class ElasticsearchLearningDemo implements AutoCloseable {
    public record Topic(String id, String name, String explanation) {}
    public record Step(String method, String path, String explanation, String request, int status, JsonNode response) {}
    public record Report(String lesson, String index, List<Step> steps, Map<String, Boolean> checks) {}
    private final URI endpoint;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    private final JsonMapper json = JsonMapper.builder().build();

    public ElasticsearchLearningDemo(String endpoint) {
        this.endpoint = URI.create(endpoint.replaceAll("/+$", ""));
        if (!List.of("http", "https").contains(this.endpoint.getScheme()) || this.endpoint.getHost() == null
                || this.endpoint.getUserInfo() != null || this.endpoint.getQuery() != null
                || this.endpoint.getFragment() != null || !this.endpoint.getPath().isEmpty()) {
            throw new IllegalArgumentException("ES endpoint must be an http(s) origin without credentials or a path");
        }
    }

    public static List<Topic> topics() {
        return List.of(
                new Topic("crud", "索引、Mapping 与文档 CRUD", "显式 text/keyword/integer Mapping；实时 GET 与 refresh 后搜索；部分更新和删除。"),
                new Topic("search", "分词与 Query DSL", "standard analyzer；match 与 term 对照；keyword 精确匹配；bool/filter/range、排序和高亮。"),
                new Topic("aggregation", "分组与数值聚合", "keyword terms 分桶、每组 avg 和全局 stats；size=0 只返回聚合。"));
    }

    public static String configuredEndpoint() {
        return System.getenv().getOrDefault("KUMA_LAB_ES_URL", "http://127.0.0.1:19200");
    }

    public static void main(String[] args) throws Exception {
        try (var lab = new ElasticsearchLearningDemo(configuredEndpoint())) {
            System.out.println(lab.json.writeValueAsString(lab.run(args.length == 0 ? "all" : args[0])));
        }
    }

    public Report run(String lesson) throws Exception {
        if (!List.of("all", "crud", "search", "aggregation").contains(lesson)) {
            throw new IllegalArgumentException("Choose all, crud, search or aggregation");
        }
        String index = "kuma_lab_es_" + UUID.randomUUID().toString().replace("-", "");
        List<Step> steps = new ArrayList<>();
        Map<String, Boolean> checks = new LinkedHashMap<>();
        Exception failure = null;
        // Cleanup only an index whose creation we initiated; never accept user supplied index names.
        boolean creationAttempted = false;
        try {
            creationAttempted = true;
            var created = request(steps, "PUT", "/" + index, "创建单分片、零副本的独立索引；禁用自动 refresh 以稳定观察搜索可见性。", """
                    {"settings":{"number_of_shards":1,"number_of_replicas":0,"refresh_interval":"-1"},
                     "mappings":{"dynamic":"strict","properties":{"title":{"type":"text","analyzer":"standard"},
                     "category":{"type":"keyword"},"price":{"type":"integer"},"stock":{"type":"integer"}}}}
                    """, 200);
            check(checks, "indexCreated", created.path("acknowledged").asBoolean());
            var mapping = request(steps, "GET", "/" + index + "/_mapping", "读取服务端实际 Mapping；text 用于分词，keyword 用于精确匹配和聚合。", null, 200);
            check(checks, "mappingVerified", mapping.path(index).path("mappings").path("properties").path("title").path("type").asString("").equals("text")
                    && mapping.path(index).path("mappings").path("properties").path("category").path("type").asString("").equals("keyword"));
            var bulk = request(steps, "POST", "/" + index + "/_bulk", "批量写入三条固定样本；NDJSON 必须以换行结尾，逐项错误也需检查。", """
                    {"index":{"_id":"1"}}
                    {"title":"Elasticsearch search basics","category":"search","price":20,"stock":8}
                    {"index":{"_id":"2"}}
                    {"title":"Advanced Elasticsearch search","category":"search","price":40,"stock":4}
                    {"index":{"_id":"3"}}
                    {"title":"PostgreSQL database transactions","category":"database","price":90,"stock":2}
                    """, 200);
            check(checks, "bulkSucceeded", !bulk.path("errors").asBoolean(true) && bulk.path("items").size() == 3);
            var get = request(steps, "GET", "/" + index + "/_doc/1", "GET 文档是实时读取：未 refresh 也可以查到刚写入的文档。", null, 200);
            check(checks, "realtimeGet", get.path("found").asBoolean() && get.path("_source").path("price").asInt() == 20);
            var before = request(steps, "GET", "/" + index + "/_count", "自动 refresh 已关闭，搜索端此时尚不可见。", null, 200);
            check(checks, "invisibleBeforeRefresh", before.path("count").asInt(-1) == 0);
            request(steps, "POST", "/" + index + "/_refresh", "仅 refresh 本次索引，随后搜索能看到三条文档。", null, 200);
            var after = request(steps, "GET", "/" + index + "/_count", "对照 refresh 后的文档数量。", null, 200);
            check(checks, "visibleAfterRefresh", after.path("count").asInt() == 3);

            if (lesson.equals("all") || lesson.equals("search")) search(index, steps, checks);
            if (lesson.equals("all") || lesson.equals("aggregation")) aggregate(index, steps, checks);
            if (lesson.equals("all") || lesson.equals("crud")) crud(index, steps, checks);
        } catch (Exception ex) {
            failure = new IOException("ES experiment " + index + " failed: " + ex.getMessage(), ex);
            throw failure;
        } finally {
            if (creationAttempted) {
                try {
                    var deleted = request(steps, "DELETE", "/" + index, "清理本次独立索引，保留报告中的请求与响应证据。", null, 200, 404);
                    check(checks, "indexCleaned", deleted.path("acknowledged").asBoolean() || deleted.path("status").asInt() == 404);
                } catch (Exception cleanup) {
                    if (failure != null) failure.addSuppressed(cleanup);
                    else throw cleanup;
                }
            }
        }
        return new Report(lesson, index, List.copyOf(steps), Map.copyOf(checks));
    }

    private void search(String index, List<Step> steps, Map<String, Boolean> checks) throws Exception {
        var analyzed = request(steps, "POST", "/" + index + "/_analyze", "standard 分词将 Elasticsearch Search 转为小写 token。", """
                {"analyzer":"standard","text":"Elasticsearch Search"}
                """, 200);
        check(checks, "analyzerTokens", analyzed.path("tokens").size() == 2
                && analyzed.path("tokens").get(0).path("token").asString().equals("elasticsearch"));
        var match = query(index, steps, "match 会分析输入；大写 Elasticsearch 仍命中两个文档。", """
                {"query":{"match":{"title":"Elasticsearch"}}}
                """);
        check(checks, "matchAnalyzesInput", total(match) == 2);
        var term = query(index, steps, "term 不分析输入；倒排索引是小写 token，因此大写词不命中。", """
                {"query":{"term":{"title":"Elasticsearch"}}}
                """);
        check(checks, "termDoesNotAnalyze", total(term) == 0);
        var lower = query(index, steps, "小写 term 对照：精确命中倒排索引中的 token。", """
                {"query":{"term":{"title":"elasticsearch"}}}
                """);
        check(checks, "termMatchesToken", total(lower) == 2);
        var filtered = query(index, steps, "bool must 做全文匹配，filter 做 keyword/range 过滤；按 price 排序并高亮 title。", """
                {"query":{"bool":{"must":[{"match":{"title":"search"}}],"filter":[
                 {"term":{"category":"search"}},{"range":{"price":{"gte":30}}}]}},
                 "sort":[{"price":"asc"}],"highlight":{"fields":{"title":{}}}}
                """);
        check(checks, "filteredAndHighlighted", total(filtered) == 1
                && filtered.path("hits").path("hits").get(0).path("_id").asString().equals("2")
                && filtered.path("hits").path("hits").get(0).path("highlight").path("title").size() > 0);
    }

    private void aggregate(String index, List<Step> steps, Map<String, Boolean> checks) throws Exception {
        var result = query(index, steps, "terms 按 category 分桶；每组平均价格与全局 stats；size=0 不返回文档。", """
                {"size":0,"aggs":{"categories":{"terms":{"field":"category","size":10,"order":{"_key":"asc"}},
                 "aggs":{"average_price":{"avg":{"field":"price"}}}},"price_stats":{"stats":{"field":"price"}}}}
                """);
        var buckets = result.path("aggregations").path("categories").path("buckets");
        boolean groups = buckets.size() == 2;
        for (var bucket : buckets) {
            String category = bucket.path("key").asString();
            groups &= switch (category) {
                case "search" -> bucket.path("doc_count").asInt() == 2 && bucket.path("average_price").path("value").asDouble() == 30;
                case "database" -> bucket.path("doc_count").asInt() == 1 && bucket.path("average_price").path("value").asDouble() == 90;
                default -> false;
            };
        }
        check(checks, "categoryBuckets", groups);
        var stats = result.path("aggregations").path("price_stats");
        check(checks, "priceStatistics", stats.path("count").asInt() == 3 && stats.path("sum").asDouble() == 150
                && stats.path("avg").asDouble() == 50 && stats.path("min").asDouble() == 20 && stats.path("max").asDouble() == 90);
        check(checks, "aggregationOnly", result.path("hits").path("hits").isArray() && result.path("hits").path("hits").isEmpty());
    }

    private void crud(String index, List<Step> steps, Map<String, Boolean> checks) throws Exception {
        var updated = request(steps, "POST", "/" + index + "/_update/1?refresh=true", "部分更新价格；refresh=true 让随后的搜索立即可见。", "{\"doc\":{\"price\":25}}", 200);
        var get = request(steps, "GET", "/" + index + "/_doc/1", "查回更新后的价格，并验证未更新字段仍保留。", null, 200);
        check(checks, "partialUpdate", updated.path("result").asString().equals("updated")
                && get.path("_source").path("price").asInt() == 25 && get.path("_source").path("stock").asInt() == 8);
        request(steps, "DELETE", "/" + index + "/_doc/3?refresh=true", "删除第3条样本，仅操作本次索引。", null, 200);
        var missing = request(steps, "GET", "/" + index + "/_doc/3", "删除后实时 GET 返回404且 found=false，这是预期结果。", null, 404);
        var remaining = request(steps, "GET", "/" + index + "/_count", "refresh 后搜索端只剩两条文档。", null, 200);
        check(checks, "documentDeleted", missing.has("found") && !missing.path("found").asBoolean(true) && remaining.path("count").asInt() == 2);
    }

    private JsonNode query(String index, List<Step> steps, String explanation, String body) throws Exception {
        var response = request(steps, "POST", "/" + index + "/_search", explanation, body, 200);
        if (response.path("timed_out").asBoolean() || response.path("_shards").path("failed").asInt() != 0) {
            throw new IOException("ES search timed out or returned failed shards");
        }
        return response;
    }

    private static int total(JsonNode response) { return response.path("hits").path("total").path("value").asInt(-1); }

    private JsonNode request(List<Step> steps, String method, String path, String explanation, String body, int... expected) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create(endpoint + path)).timeout(Duration.ofSeconds(5))
                .header("Content-Type", path.endsWith("/_bulk") ? "application/x-ndjson" : "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        var response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        JsonNode parsed = json.readTree(response.body());
        steps.add(new Step(method, path, explanation, body, response.statusCode(), parsed));
        for (int status : expected) if (status == response.statusCode()) return parsed;
        throw new IOException("ES " + method + " " + path + " returned HTTP " + response.statusCode() + ": " + response.body());
    }

    private static void check(Map<String, Boolean> checks, String name, boolean passed) {
        checks.put(name, passed);
        if (!passed) throw new IllegalStateException("ES experiment check failed: " + name);
    }

    @Override public void close() { client.close(); }
}
