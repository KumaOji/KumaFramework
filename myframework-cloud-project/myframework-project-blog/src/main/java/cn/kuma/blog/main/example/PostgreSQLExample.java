package cn.kuma.blog.main.example;//package cn.kuma.blog.main.example;
//
//import cn.kuma.blog.framework.mybatisplus.util.SchemaContext;
//import com.baomidou.dynamic.datasource.annotation.DS;
//import jakarta.annotation.Resource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//
///**
// * PostgreSQL 数据源切换示例
// *
// * 本项目使用 dynamic-datasource 实现多数据源切换
// * 配置的数据源：
// * - mysql: 默认数据源（@Primary）
// * - postgresql: PostgreSQL 数据源
// *
// * @author Kuma
// * @version 1.0
// */
//@Service
//public class PostgreSQLExample {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    /**
//     * 示例1：在方法级别切换到 PostgreSQL
//     * 使用 @DS("postgresql") 注解切换到 PostgreSQL 数据源
//     *
//     * @return 查询结果
//     */
//    @DS("postgresql")
//    public List<Map<String, Object>> queryFromPostgreSQL() {
//        // 这个方法会使用 PostgreSQL 数据源执行查询
//        String sql = "SELECT version() as version, current_database() as database";
//        return jdbcTemplate.queryForList(sql);
//    }
//
//    /**
//     * 示例2：在类级别切换到 PostgreSQL
//     * 整个类的所有方法都会使用 PostgreSQL 数据源
//     */
//    @DS("postgresql")
//    public static class PostgreSQLService {
//
//        @Autowired
//        private JdbcTemplate jdbcTemplate;
//
//        /**
//         * 查询 PostgreSQL 中的表列表
//         */
//        public List<Map<String, Object>> getTables() {
//            String sql = "SELECT table_name FROM information_schema.tables " +
//                        "WHERE table_schema = 'public' ORDER BY table_name";
//            return jdbcTemplate.queryForList(sql);
//        }
//
//        /**
//         * 查询 PostgreSQL 中的用户信息
//         */
//        public List<Map<String, Object>> getUsers() {
//            String sql = "SELECT usename, usesysid, usecreatedb, usesuper " +
//                        "FROM pg_user ORDER BY usename";
//            return jdbcTemplate.queryForList(sql);
//        }
//    }
//
//    /**
//     * 示例3：在方法中动态切换回 MySQL
//     * 即使类级别使用了 @DS("postgresql")，也可以使用 @DS("mysql") 切换回 MySQL
//     */
//    @DS("postgresql")
//    public static class MixedDataSourceService {
//
//        @Resource
//        private JdbcTemplate jdbcTemplate;
//
//        /**
//         * 使用 PostgreSQL 查询
//         */
//        public List<Map<String, Object>> queryFromPostgreSQL() {
//            String sql = "SELECT current_database() as database";
//            return jdbcTemplate.queryForList(sql);
//        }
//
//        /**
//         * 切换到 MySQL 查询
//         */
//        @DS("mysql")
//        public List<Map<String, Object>> queryFromMySQL() {
////            SchemaContext.withSchema(SCHEMA_NAME, () -> {
////                LocalDateTime now = LocalDateTime.now();
////                source.setCreateTime(now);
////                source.setUpdateTime(now);
////                sourceMapper.insert(source);
////                result[0] = source.getId();
////            });
//            String sql = "SELECT DATABASE() as database";
//            return jdbcTemplate.queryForList(sql);
//        }
//    }
//
//    /**
//     * 示例4：使用 MyBatis Mapper 切换数据源
//     * 在 Mapper 接口或方法上使用 @DS 注解
//     *
//     * 示例代码（需要在 Mapper 接口中使用）：
//     *
//     * @Mapper
//     * @DS("postgresql")
//     * public interface PostgreSQLMapper {
//     *     @Select("SELECT * FROM your_table")
//     *     List<YourEntity> selectAll();
//     * }
//     */
//
//    /**
//     * 示例5：在 Service 方法中使用
//     * 不指定 @DS 注解的方法会使用默认数据源（MySQL）
//     */
//    public List<Map<String, Object>> queryFromDefaultDataSource() {
//        // 使用默认数据源（MySQL）
//        String sql = "SELECT DATABASE() as database";
//        return jdbcTemplate.queryForList(sql);
//    }
//
//    /**
//     * 切换到 PostgreSQL 执行查询
//     */
//    @DS("postgresql")
//    public List<Map<String, Object>> queryFromPostgreSQLDataSource() {
//        // 使用 PostgreSQL 数据源
//        String sql = "SELECT current_database() as database";
//        return jdbcTemplate.queryForList(sql);
//    }
//}
