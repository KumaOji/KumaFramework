package com.kuma.cloud.lab.starter.support;

import com.kuma.boot.common.constant.StarterNameConstants;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Starter 与 classpath 锚点类的映射表，用于判断依赖是否引入。
 */
public final class StarterAnchorRegistry {

    private static final Map<String, String> ANCHORS = new LinkedHashMap<>();

    static {
        register(StarterNameConstants.ACTUATOR_STARTER,
                "com.kuma.boot.actuator.endpoint.audit.autoconfigure.AuditEndPointAutoConfiguration");
        register(StarterNameConstants.AI_STARTER, "com.kuma.boot.ai.autoconfigure.AiChatAutoConfiguration");
        register(StarterNameConstants.AUDIT_LOG_STARTER,
                "com.kuma.boot.auditlog.autoconfigure.AuditLogAutoConfiguration");
        register(StarterNameConstants.CACHE_CAFFEINE_STARTER,
                "com.kuma.boot.cache.caffeine.autoconfigure.CaffeineCacheAutoConfiguration");
        register(StarterNameConstants.CACHE_JETCACHE_STARTER,
                "com.kuma.boot.cache.jetcache.autoconfigure.JetCacheAutoConfiguration");
        register(StarterNameConstants.CACHE_REDIS_STARTER,
                "com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration");
        register(StarterNameConstants.COMMON_STARTER, "com.kuma.boot.common.constant.StarterNameConstants");
        register(StarterNameConstants.CORE_STARTER, "com.kuma.boot.core.autoconfigure.CoreAutoConfiguration");
        register(StarterNameConstants.DATA_DATASOURCE_STARTER,
                "com.kuma.boot.data.datasource.autoconfigure.KmcDataSourceAutoConfiguration");
        register(StarterNameConstants.DATA_ELASTICSEARCH_STARTER,
                "com.kuma.boot.data.elasticsearch.autoconfigure.ElasticsearchAutoConfiguration");
        register(StarterNameConstants.DATA_JPA_STARTER, "com.kuma.boot.data.jpa.autoconfigure.JpaAutoConfiguration");
        register(StarterNameConstants.DATA_MONGODB_STARTER,
                "com.kuma.boot.data.mongodb.autoconfigure.MongoAutoConfiguration");
        register(StarterNameConstants.DATA_MYBATIS_STARTER,
                "com.kuma.boot.data.mybatis.autoconfigure.MybatisAutoConfiguration");
        register(StarterNameConstants.DATA_VECTOR_STARTER,
                "com.kuma.boot.data.vector.autoconfigure.VectorStoreAutoConfiguration");
        register(StarterNameConstants.DINGTALK_STARTER,
                "com.kuma.boot.dingtalk.autoconfigure.DingTalkAutoConfiguration");
        register(StarterNameConstants.ENCRYPT_STARTER, "com.kuma.boot.encrypt.autoconfigure.EncryptAutoConfiguration");
        register(StarterNameConstants.EVENTBUS_STARTER,
                "com.kuma.boot.eventbus.autoconfigure.EventBusAutoConfiguration");
        register(StarterNameConstants.FILE_TRANSFER_STARTER,
                "com.kuma.boot.transfer.autoconfigure.FileTransferAutoConfiguration");
        register(StarterNameConstants.FLOWENGINE_STARTER,
                "com.kuma.boot.flowengine.autoconfigure.FlowEngineAutoConfiguration");
        register(StarterNameConstants.GRAPHQL_STARTER, "com.kuma.boot.graphql.autoconfigure.GraphqlAutoConfiguration");
        register(StarterNameConstants.GRPC_STARTER,
                "com.kuma.boot.grpc.spring.config.GrpcClientAutoConfiguration");
        register(StarterNameConstants.I18N_STARTER, "com.kuma.boot.i18n.config.I18nAutoConfiguration");
        register(StarterNameConstants.IDEMPOTENT_STARTER,
                "com.kuma.boot.idempotent.autoconfigure.IdempotentAutoConfiguration");
        register(StarterNameConstants.IDGENERATOR_STARTER,
                "com.kuma.boot.idgenerator.autoconfigure.IdGeneratorAutoConfiguration");
        register(StarterNameConstants.IP2REGION_STARTER,
                "com.kuma.boot.ip2region.autoconfigure.Ip2regionAutoConfiguration");
        register(StarterNameConstants.JOB_POWERJOB_STARTER,
                "com.kuma.boot.job.powerjob.autoconfigure.PowerJobAutoConfiguration");
        register(StarterNameConstants.JOB_QUARTZ_STARTER,
                "com.kuma.boot.job.quartz.autoconfigure.QuartzAutoConfiguration");
        register(StarterNameConstants.JOB_XXL_STARTER, "com.kuma.boot.job.xxl.autoconfigure.XxlJobAutoConfiguration");
        register(StarterNameConstants.LOCK_STARTER, "com.kuma.boot.lock.autoconfigure.LockAutoConfiguration");
        register(StarterNameConstants.LOGGER_STARTER, "com.kuma.boot.logger.autoconfigure.LoggerAutoConfiguration");
        register(StarterNameConstants.MAIL_STARTER, "com.kuma.boot.mail.autoconfigure.MailAutoConfiguration");
        register(StarterNameConstants.MCP_STARTER, "com.kuma.boot.mcp.autoconfigure.McpServerAutoConfiguration");
        register(StarterNameConstants.METRICS_STARTER, "com.kuma.boot.metrics.autoconfigure.MetricsAutoConfiguration");
        register(StarterNameConstants.MONITOR_STARTER, "com.kuma.boot.monitor.autoconfigure.MonitorAutoConfiguration");
        register(StarterNameConstants.MQ_KAFKA_STARTER,
                "com.kuma.boot.mq.kafka.kafkafactory.MqAutoConfiguration");
        register(StarterNameConstants.MQ_RABBITMQ_STARTER,
                "com.kuma.boot.mq.rabbitmq.autoconfigure.RabbitMQMessageQueueAutoConfiguration");
        register(StarterNameConstants.MQ_ROCKETMQ_STARTER,
                "com.kuma.boot.mq.rocketmq.autoconfigure.RocketMQAutoConfiguration");
        register(StarterNameConstants.MQTT_STARTER, "com.kuma.boot.mqtt.autoconfigure.MqttAutoConfiguration");
        register(StarterNameConstants.MULTI_TENANT_STARTER,
                "com.kuma.boot.tenant.autoconfigure.TenantAutoConfiguration");
        register(StarterNameConstants.OFFICE_STARTER, "com.kuma.boot.office.autoconfigure.OfficeAutoConfiguration");
        register(StarterNameConstants.OPENAPI_STARTER, "com.kuma.boot.openapi.autoconfigure.OpenApiAutoConfiguration");
        register(StarterNameConstants.OSS_ALIYUN_STARTER,
                "com.kuma.boot.oss.aliyun.support.AliyunOssConfiguration");
        register(StarterNameConstants.OSS_COS_STARTER, "com.kuma.boot.oss.cos.support.CosOssConfiguration");
        register(StarterNameConstants.OSS_MINIO_STARTER,
                "com.kuma.boot.oss.minio.support.MinioOssConfiguration");
        register(StarterNameConstants.OSS_QINIU_STARTER,
                "com.kuma.boot.oss.qiniu.support.QiniuOssConfiguration");
        register(StarterNameConstants.OTEL_STARTER, "com.kuma.boot.otel.configuration.OtelAutoConfiguration");
        register(StarterNameConstants.PINYIN_STARTER, "com.kuma.boot.pinyin.autoconfigure.PinyinAutoConfiguration");
        register(StarterNameConstants.PROMETHEUS_STARTER,
                "com.kuma.boot.prometheus.autoconfigure.PrometheusAutoConfiguration");
        register(StarterNameConstants.RATELIMIT_STARTER,
                "com.kuma.boot.ratelimit.autoconfigure.RateLimitAutoConfiguration");
        register(StarterNameConstants.RETRY_STARTER, "com.kuma.boot.retry.autoconfigure.RetryAutoConfiguration");
        register(StarterNameConstants.SEATA_STARTER, "com.kuma.boot.seata.autoconfigure.SeataAutoConfiguration");
        register(StarterNameConstants.SECURITY_SATOKEN_STARTER,
                "com.kuma.boot.security.satoken.configuration.SaTokenAutoConfiguration");
        register(StarterNameConstants.SECURITY_SPRINGSECURITY_STARTER,
                "com.kuma.boot.security.spring.autoconfigure.SpringSecurityAutoConfiguration");
        register(StarterNameConstants.SENSITIVE_STARTER,
                "com.kuma.boot.sensitive.autoconfigure.SensitiveAutoConfiguration");
        register(StarterNameConstants.SENTINEL_STARTER, "com.kuma.boot.sentinel.autoconfigure.SentinelAutoConfiguration");
        register(StarterNameConstants.SESSION_STARTER, "com.kuma.boot.session.autoconfigure.SessionAutoConfiguration");
        register(StarterNameConstants.SIGN_STARTER, "com.kuma.boot.sign.autoconfigure.SignAutoConfiguration");
        register(StarterNameConstants.SMS_ALIYUN_STARTER,
                "com.kuma.boot.sms.aliyun.configuration.AliyunSmsAutoConfiguration");
        register(StarterNameConstants.SMS_COMMON_STARTER,
                "com.kuma.boot.sms.common.configuration.SmsAutoConfiguration");
        register(StarterNameConstants.SMS_TENCENT_STARTER,
                "com.kuma.boot.sms.tencent.configuration.TencentSmsAutoConfiguration");
        register(StarterNameConstants.SPRINGDOC_STARTER,
                "com.kuma.boot.springdoc.autoconfigure.SpringdocAutoConfiguration");
        register(StarterNameConstants.STATEMACHINE_STARTER,
                "com.kuma.boot.statemachine.autoconfigure.StateMachineAutoConfiguration");
        register(StarterNameConstants.SSE_STARTER, "com.kuma.boot.sse.autoconfigure.SseAutoConfiguration");
        register(StarterNameConstants.THREADPOOL_STARTER,
                "com.kuma.boot.threadpool.autoconfigure.ThreadPoolAutoConfiguration");
        register(StarterNameConstants.TOTP_STARTER, "com.kuma.boot.totp.autoconfigure.TotpAutoConfiguration");
        register(StarterNameConstants.TRANSLATION_STARTER,
                "com.kuma.boot.translation.autoconfigure.TranslationAutoConfiguration");
        register(StarterNameConstants.USERAGENT_STARTER,
                "com.kuma.boot.useragent.autoconfigure.UserAgentAutoConfiguration");
        register(StarterNameConstants.WEB_STARTER, "com.kuma.boot.web.autoconfigure.ServletAutoConfiguration");
        register(StarterNameConstants.WEBFLUX_STARTER, "com.kuma.boot.webflux.autoconfigure.WebFluxAutoConfiguration");
        register(StarterNameConstants.WEBSOCKET_STARTER,
                "com.kuma.boot.websocket.autoconfigure.WebSocketAutoConfiguration");
        register(StarterNameConstants.XSS_STARTER, "com.kuma.boot.xss.autoconfigure.XssAutoConfiguration");
    }

    private StarterAnchorRegistry() {
    }

    public static Optional<String> anchorClass(String starterName) {
        return Optional.ofNullable(ANCHORS.get(starterName));
    }

    public static boolean isOnClasspath(String starterName) {
        return anchorClass(starterName).map(StarterAnchorRegistry::isClassPresent).orElse(false);
    }

    public static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, StarterAnchorRegistry.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static String categoryOf(String starterName) {
        if (starterName.startsWith("kuma-cloud-starter-")) {
            return "cloud";
        }
        String stripped = starterName.replace("kuma-boot-starter-", "");
        int separator = stripped.indexOf('-');
        if (separator <= 0) {
            return stripped;
        }
        return stripped.substring(0, separator);
    }

    private static void register(String starterName, String anchorClass) {
        ANCHORS.put(starterName, anchorClass);
    }

}
