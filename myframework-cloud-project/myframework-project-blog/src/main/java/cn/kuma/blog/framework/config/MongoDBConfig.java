package cn.kuma.blog.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * MongoDB 配置类
 * 配置 MongoDB 连接和 MongoTemplate
 *
 * 说明：
 * - MongoDB Repository 自动扫描已启用（在 application.yml 中配置）
 * - 可使用 MongoTemplate 或 MongoDB Repository 进行 MongoDB 操作
 * - 配置可通过 application.yml 中的 spring.data.mongodb.* 覆盖默认值
 *
 * <pre>
 * @Autowired
 * private MongoTemplate mongoTemplate;
 * </pre>
 *
 * @author Kuma
 * @version 1.0
 */
@Configuration
@SuppressWarnings("all")
public class MongoDBConfig {

    // ==================== MongoDB 配置（可通过配置文件覆盖） ====================
    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:mongodb}")
    private String database;

    /**
     * 创建 MongoDB 数据库工厂
     */
    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        String connectionString = mongoUri;
        if (!connectionString.contains("/") || connectionString.endsWith("/") ||
            (connectionString.split("/").length == 3 && !connectionString.split("/")[2].contains("?"))) {
            if (!connectionString.endsWith("/")) {
                connectionString += "/";
            }
            connectionString += database;
        }
        return new SimpleMongoClientDatabaseFactory(connectionString);
    }

    /**
     * 创建 MongoTemplate
     * 用于操作 MongoDB 数据库
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
