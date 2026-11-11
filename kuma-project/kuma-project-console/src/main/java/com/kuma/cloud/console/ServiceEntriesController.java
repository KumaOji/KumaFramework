package com.kuma.cloud.console;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class ServiceEntriesController {
    private final Environment config;
    private final ConsoleProperties properties;
    private final LifeLifecycle life;
    public ServiceEntriesController(Environment config, ConsoleProperties properties, LifeLifecycle life) {
        this.config = config; this.properties = properties; this.life = life;
    }
    public record Entry(String id, String name, String group, String url, boolean externalOnly, String description, boolean readOnly) {}

    @GetMapping("/api/service-entries")
    public List<Entry> entries() {
        var entries = new ArrayList<Entry>();
        entries.add(entry("grafana", "Grafana", "监控与告警", config.getProperty("console.panels.grafana-url", "http://localhost:3000/"), false, "WSL · base · 指标、日志与调用链"));
        entries.add(entry("prometheus", "Prometheus", "监控与告警", "http://localhost:9090", false, "WSL · base · 指标查询"));
        entries.add(entry("alertmanager", "Alertmanager", "监控与告警", "http://localhost:9093", false, "WSL · base · 告警"));
        entries.add(entry("skywalking", "SkyWalking", "监控与告警", "http://localhost:12880", false, "WSL · base · 链路追踪"));
        entries.add(entry("n8n", "n8n · 工作流自动化", "WSL 应用", "http://localhost:5678", true, "WSL · base"));
        entries.add(entry("nacos", "Nacos · 配置与注册中心", "WSL 应用", "http://localhost:8848/nacos/", true, "WSL · base"));
        entries.add(entry("qdrant", "Qdrant · 向量数据库", "WSL 应用", "http://localhost:6333/dashboard", true, "WSL · base"));
        var lifeUrls = life.urls();
        if (lifeUrls.containsKey("qinglongUrl")) entries.add(entry("qinglong", "Life · 青龙", "WSL 应用", lifeUrls.get("qinglongUrl"), true, "WSL · life-deals · 定时任务"));
        if (lifeUrls.containsKey("rsshubUrl")) entries.add(entry("rsshub", "Life · RSSHub", "WSL 应用", lifeUrls.get("rsshubUrl"), false, "WSL · life-deals · RSS 订阅"));
        entries.add(entry("dsh", "DeepSeek Harness", "本机项目", port("console.dsh.port", 3080), false, "本机 · AI 工作台"));
        entries.add(entry("blog-frontend", "Blog", "本机项目", port("console.blog.port", 4321), false, "本机 · Blog 前端"));
        for (var project : properties.projects()) {
            entries.add(entry("project-" + project.name().toLowerCase(Locale.ROOT), project.name() + " · API", "项目接口", project.url(), true, "本机 · 项目服务"));
            if (project.managementUrl() != null && !project.managementUrl().isBlank())
                entries.add(entry("management-" + project.name().toLowerCase(Locale.ROOT), project.name() + " · 管理接口", "项目接口", project.actuatorUrl() + "/actuator", true, "本机 · Actuator"));
        }
        entries.add(entry("loki", "Loki · API", "采集接口", "http://localhost:3100/ready", true, "WSL · 日志界面在 Grafana"));
        entries.add(entry("zipkin", "SkyWalking · Zipkin API", "采集接口", "http://localhost:9412/zipkin/api/v2/services", true, "WSL · 链路查询接口"));
        entries.add(entry("otlp", "OTLP · HTTP", "采集接口", "http://localhost:4318/v1/traces", true, "WSL · POST 链路采集接口"));
        entries.add(connection("mysql", "MySQL", "mysql://mysql.base:3306"));
        entries.add(connection("postgresql", "PostgreSQL", "postgresql://postgresql.base:5432"));
        entries.add(connection("redis", "Redis", "redis://redis.base:6379"));
        entries.add(connection("kafka", "Kafka", "kafka://kafka-kafka-bootstrap.base:9092"));
        entries.add(connection("qdrant-grpc", "Qdrant · gRPC", "grpc://qdrant.base:6334"));
        entries.add(connection("otlp-grpc", "OTLP · gRPC", "grpc://otel-collector.base:4317"));
        entries.add(connection("skywalking-agent", "SkyWalking · Agent", "grpc://skywalking-oap.base:11800"));
        entries.add(connection("life-redis", "Life · Redis", "redis://redis.life-deals:6379"));
        return entries;
    }
    private String port(String key, int fallback) { return "http://localhost:" + config.getProperty(key, Integer.class, fallback); }
    private Entry entry(String id, String name, String group, String url, boolean external, String description) {
        return new Entry(id, name, group, config.getProperty("console.panels." + id + "-url", url), external, description, false);
    }
    private Entry connection(String id, String name, String url) {
        return new Entry(id, name, "集群连接（非网页）", url, true, "WSL / k3s 集群内地址 · 使用专用客户端", true);
    }
}
