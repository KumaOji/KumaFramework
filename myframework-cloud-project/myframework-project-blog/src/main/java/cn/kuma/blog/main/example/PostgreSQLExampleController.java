package cn.kuma.blog.main.example;//package cn.kuma.blog.main.example;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * PostgreSQL 数据源切换示例控制器
// *
// * 演示如何使用 @DS 注解切换到 PostgreSQL 数据源
// *
// * @author Kuma
// * @version 1.0
// */
//@Tag(name = "PostgreSQL示例", description = "PostgreSQL 数据源切换示例")
//@RestController
//@RequestMapping("/example/postgresql")
//public class PostgreSQLExampleController {
//
//    @Autowired
//    private PostgreSQLExample postgreSQLExample;
//
//    @Autowired
//    private PostgreSQLExample.PostgreSQLService postgreSQLService;
//
//    @Autowired
//    private PostgreSQLExample.MixedDataSourceService mixedDataSourceService;
//
//    /**
//     * 示例1：查询 PostgreSQL 版本信息
//     */
//    @Operation(summary = "查询PostgreSQL版本", description = "使用 @DS 注解切换到 PostgreSQL 数据源并查询版本信息")
//    @GetMapping("/version")
//    public List<Map<String, Object>> getPostgreSQLVersion() {
//        return postgreSQLExample.queryFromPostgreSQL();
//    }
//
//    /**
//     * 示例2：查询 PostgreSQL 中的表列表
//     */
//    @Operation(summary = "查询PostgreSQL表列表", description = "查询 PostgreSQL 数据库中的所有表")
//    @GetMapping("/tables")
//    public List<Map<String, Object>> getTables() {
//        return postgreSQLService.getTables();
//    }
//
//    /**
//     * 示例3：查询 PostgreSQL 中的用户信息
//     */
//    @Operation(summary = "查询PostgreSQL用户", description = "查询 PostgreSQL 数据库中的用户信息")
//    @GetMapping("/users")
//    public List<Map<String, Object>> getUsers() {
//        return postgreSQLService.getUsers();
//    }
//
//    /**
//     * 示例4：混合数据源查询
//     * 先查询 PostgreSQL，再查询 MySQL
//     */
//    @Operation(summary = "混合数据源查询", description = "演示在同一方法中切换不同数据源")
//    @GetMapping("/mixed")
//    public Map<String, Object> getMixedData() {
//        Map<String, Object> result = new java.util.HashMap<>();
//        result.put("postgresql", mixedDataSourceService.queryFromPostgreSQL());
//        result.put("mysql", mixedDataSourceService.queryFromMySQL());
//        return result;
//    }
//
//    /**
//     * 示例5：默认数据源查询（MySQL）
//     */
//    @Operation(summary = "默认数据源查询", description = "使用默认数据源（MySQL）查询")
//    @GetMapping("/default")
//    public List<Map<String, Object>> getDefaultDataSource() {
//        return postgreSQLExample.queryFromDefaultDataSource();
//    }
//}
