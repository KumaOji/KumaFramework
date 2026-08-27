package com.kuma.cloud.base.mybatis;

import com.kuma.cloud.base.mybatis.entity.User;
import com.kuma.cloud.base.mybatis.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;

/**
 * MyBatis 两级缓存运用示例
 *
 * <pre>
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │                   MyBatis 缓存体系总览                               │
 * ├────────────┬────────────────────────────────────────────────────────┤
 * │ 级别       │ 描述                                                    │
 * ├────────────┼────────────────────────────────────────────────────────┤
 * │ 一级缓存   │ SqlSession 级别，自动开启，无需配置                       │
 * │ (Local)    │ 同一 Session 重复查询直接命中，关闭/提交/写操作后失效       │
 * ├────────────┼────────────────────────────────────────────────────────┤
 * │ 二级缓存   │ Mapper Namespace 级别，跨 SqlSession 共享                 │
 * │ (Shared)   │ 需要全局 cacheEnabled=true + Mapper XML 中 <cache/>      │
 * │            │ 实体必须实现 Serializable（readOnly=false 时序列化拷贝）   │
 * │            │ Session 关闭或提交后数据才写入二级缓存                     │
 * └────────────┴────────────────────────────────────────────────────────┘
 *
 * 运行时可通过控制台日志判断是否命中缓存：
 *   - "Cache Hit Ratio" = 0.0  → 未命中，实际查询数据库
 *   - "Cache Hit Ratio" > 0.0  → 命中二级缓存，未查询数据库
 *   - 一级缓存命中时不打印 SQL（日志中不出现 "Preparing:" 行）
 * </pre>
 */
class MybatisCacheTest {

    static SqlSessionFactory factory;

    // ------------------------------------------------------------------ //
    //  环境初始化                                                           //
    // ------------------------------------------------------------------ //

    @BeforeAll
    static void setUp() throws Exception {
        // 1. 从 classpath 加载全局配置，构建 SqlSessionFactory
        InputStream is = Resources.getResourceAsStream("mybatis/mybatis-config.xml");
        factory = new SqlSessionFactoryBuilder().build(is);

        // 2. 建表并写入初始数据（autoCommit=true，DDL 立即生效）
        try (SqlSession session = factory.openSession(true)) {
            Connection conn = session.getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                        CREATE TABLE IF NOT EXISTS t_user (
                          id   BIGINT PRIMARY KEY,
                          name VARCHAR(64),
                          age  INT
                        )""");
                stmt.execute("DELETE FROM t_user");
                stmt.execute("INSERT INTO t_user VALUES (1, 'Alice', 20)");
                stmt.execute("INSERT INTO t_user VALUES (2, 'Bob',   25)");
                stmt.execute("INSERT INTO t_user VALUES (3, 'Carol', 30)");
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  辅助方法                                                             //
    // ------------------------------------------------------------------ //

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.printf( "║  %-52s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    private static void log(String msg) {
        System.out.println("  [TEST] " + msg);
    }

    // ================================================================== //
    //  一级缓存（Local Cache / SqlSession 级别）                            //
    // ================================================================== //

    /**
     * 演示：同一 SqlSession 内重复查询命中一级缓存
     *
     * <p>第一次查询会向数据库发出 SQL；第二次完全相同的查询
     * 直接从 SqlSession 内部的 HashMap 返回，不产生任何数据库交互。
     *
     * <p>判断依据：控制台中第二次调用之后不会再出现 "Preparing: SELECT ..." 日志。
     */
    @Test
    void test1_L1_sameSession_hitCache() {
        printHeader("一级缓存：同一 Session 重复查询命中缓存");

        try (SqlSession session = factory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            log("第一次查询 id=1 → 发出 SQL，结果存入一级缓存");
            User first = mapper.selectById(1L);
            log("结果：" + first);

            log("第二次查询 id=1 → 命中一级缓存，不发 SQL");
            User second = mapper.selectById(1L);
            log("结果：" + second);

            // 同一缓存条目返回同一个对象引用
            log("两次结果是否为同一对象引用（== ）: " + (first == second));
        }
    }

    /**
     * 演示：同一 SqlSession 内执行写操作后一级缓存失效
     *
     * <p>执行 UPDATE 后，MyBatis 会清空当前 SqlSession 的全部一级缓存，
     * 下次查询必须重新访问数据库，确保读到最新数据。
     */
    @Test
    void test2_L1_writeOperation_invalidatesCache() {
        printHeader("一级缓存：写操作后缓存失效，重新查库");

        try (SqlSession session = factory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            log("第一次查询 id=2 → 发出 SQL");
            User before = mapper.selectById(2L);
            log("查询结果：" + before);

            log("执行 UPDATE，一级缓存被清空");
            mapper.updateAge(2L, 99);

            log("再次查询 id=2 → 缓存失效，重新发出 SQL");
            User after = mapper.selectById(2L);
            log("查询结果：" + after);
            log("age 已更新为 99：" + (after.getAge() == 99));

            // 回滚，不影响后续测试
            session.rollback();
        }
    }

    /**
     * 演示：不同 SqlSession 无法共享一级缓存
     *
     * <p>每个 SqlSession 有独立的本地缓存，Session A 的查询结果对 Session B 不可见。
     * 两个 Session 查询同一条数据时都会发出 SQL（二级缓存未命中时）。
     */
    @Test
    void test3_L1_differentSession_noCacheSharing() {
        printHeader("一级缓存：不同 Session 不共享，各自查库");

        try (SqlSession sessionA = factory.openSession();
             SqlSession sessionB = factory.openSession()) {

            UserMapper mapperA = sessionA.getMapper(UserMapper.class);
            UserMapper mapperB = sessionB.getMapper(UserMapper.class);

            log("Session A 查询 id=1");
            User userA = mapperA.selectById(1L);
            log("Session A 结果：" + userA);

            log("Session B 查询 id=1（一级缓存不共享，仍然发出 SQL）");
            User userB = mapperB.selectById(1L);
            log("Session B 结果：" + userB);

            log("两个 Session 的结果是同一个对象引用？" + (userA == userB));
            log("(false=正常，说明各自独立查询并创建了新对象)");
        }
    }

    // ================================================================== //
    //  二级缓存（Second-Level Cache / Namespace 级别）                      //
    // ================================================================== //

    /**
     * 演示：Session 关闭后数据写入二级缓存，第二个 Session 命中
     *
     * <p>关键点：数据在 Session <b>关闭或提交</b>后才从一级缓存刷入二级缓存。
     * 若 Session A 尚未关闭，Session B 是看不到 A 的查询结果的。
     */
    @Test
    void test4_L2_crossSession_hitCache() {
        printHeader("二级缓存：Session 关闭后写入 L2，下一 Session 命中");

        // Session 1：查询并关闭，将结果写入二级缓存
        log("=== Session 1 ===");
        try (SqlSession session1 = factory.openSession()) {
            UserMapper mapper1 = session1.getMapper(UserMapper.class);
            log("Session1 查询 id=1 → 发出 SQL，结果进入一级缓存");
            User user = mapper1.selectById(1L);
            log("Session1 结果：" + user);
        }
        // try-with-resources 自动调用 close()，一级缓存内容刷入二级缓存

        log("Session1 已关闭，其查询结果已写入二级缓存");

        // Session 2：相同查询，从二级缓存直接返回
        log("=== Session 2 ===");
        try (SqlSession session2 = factory.openSession()) {
            UserMapper mapper2 = session2.getMapper(UserMapper.class);
            log("Session2 查询 id=1 → 命中二级缓存，不发 SQL");
            log("（观察控制台：Cache Hit Ratio 应 > 0，且无 'Preparing:' 行）");
            User user = mapper2.selectById(1L);
            log("Session2 结果：" + user);
        }
    }

    /**
     * 演示：写操作清空二级缓存
     *
     * <p>在 Mapper XML 的 {@code <update>/<delete>/<insert>} 默认设置
     * {@code flushCache="true"}，执行后当前 namespace 的二级缓存会被清空。
     * 下次查询必须重新访问数据库。
     */
    @Test
    void test5_L2_writeOperation_flushCache() {
        printHeader("二级缓存：写操作后 L2 缓存被清空");

        // 先预热二级缓存
        log("=== 预热：Session A 查询 id=3，关闭后写入 L2 ===");
        try (SqlSession sessionA = factory.openSession()) {
            sessionA.getMapper(UserMapper.class).selectById(3L);
        }

        // 另一 Session 执行写操作，清空 L2
        log("=== Session B 执行 UPDATE，L2 缓存被清空 ===");
        try (SqlSession sessionB = factory.openSession()) {
            sessionB.getMapper(UserMapper.class).updateAge(3L, 88);
            sessionB.commit();  // commit 使写操作生效
        }

        // Session C 再次查询，L2 已失效，必须重新查库
        log("=== Session C 查询 id=3 → L2 已失效，重新发出 SQL ===");
        try (SqlSession sessionC = factory.openSession()) {
            User user = sessionC.getMapper(UserMapper.class).selectById(3L);
            log("Session C 结果（age 应为 88）：" + user);
            log("age 已更新为 88：" + (user.getAge() == 88));
        }

        // 恢复初始数据
        try (SqlSession cleanup = factory.openSession()) {
            cleanup.getMapper(UserMapper.class).updateAge(3L, 30);
            cleanup.commit();
        }
    }

    /**
     * 演示：{@code session.clearCache()} 手动清空一级缓存
     *
     * <p>某些场景（如循环批量操作）需要主动释放一级缓存以降低内存占用，
     * 或者需要在同一 Session 内读取到另一个 Session 已提交的最新数据。
     * 调用 {@code session.clearCache()} 只清空当前 Session 的本地缓存，
     * 不影响二级缓存。
     */
    @Test
    void test6_L1_manualClearCache() {
        printHeader("一级缓存：手动调用 clearCache() 强制重新查库");

        try (SqlSession session = factory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            log("第一次查询 id=1 → 发出 SQL，结果进入一级缓存");
            User first = mapper.selectById(1L);
            log("结果：" + first);

            log("手动调用 session.clearCache() 清空一级缓存");
            session.clearCache();

            log("再次查询 id=1 → 一级缓存已清空，重新发出 SQL");
            User second = mapper.selectById(1L);
            log("结果：" + second);

            log("两次结果是同一对象引用？" + (first == second));
            log("(false=正常，clearCache 后重新创建了对象)");
        }
    }
}
