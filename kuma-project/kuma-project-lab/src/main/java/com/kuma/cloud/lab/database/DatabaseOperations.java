package com.kuma.cloud.lab.database;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static com.kuma.cloud.lab.database.DatabaseSupport.*;

final class DatabaseOperations {
    static void crud() throws Exception {
        System.out.println("--- 数据库 CRUD：生成主键、增查改删与影响行数 ---");
        try (var db = new DatabaseSupport(); var connection = db.connection()) {
            long id;
            try (var insert = connection.prepareStatement("INSERT INTO catalog(name,category,price) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                insert.setQueryTimeout(5); insert.setString(1, "book"); insert.setString(2, "media"); insert.setBigDecimal(3, new BigDecimal("12.50"));
                check("插入一行", insert.executeUpdate() == 1, 1);
                try (var keys = insert.getGeneratedKeys()) {
                    if (!keys.next()) throw new IllegalStateException("Generated key missing");
                    id = keys.getLong(1);
                }
            }
            var rows = query(connection, "SELECT id,name,price FROM catalog WHERE id=?", id);
            check("生成主键可用于读取", id > 0 && rows.size() == 1 && rows.getFirst().get("name").equals("book"), rows);
            check("按主键更新", update(connection, "UPDATE catalog SET price=? WHERE id=?", new BigDecimal("15.00"), id) == 1, id);
            check("更新后的金额", number(connection, "SELECT price FROM catalog WHERE id=?", id) == 15, query(connection, "SELECT * FROM catalog"));
            check("删除一行", update(connection, "DELETE FROM catalog WHERE id=?", id) == 1, id);
            check("重复删除影响零行", update(connection, "DELETE FROM catalog WHERE id=?", id) == 0
                    && number(connection, "SELECT COUNT(*) FROM catalog") == 0, 0);
        }
    }

    static void binding() throws Exception {
        System.out.println("--- 数据库参数绑定：引号、注入形状输入与NULL ---");
        try (var db = new DatabaseSupport(); var connection = db.connection()) {
            String input = "x' OR '1'='1";
            check("参数中的SQL形状文本仍为字面值", query(connection, "SELECT id FROM accounts WHERE owner=?", input).isEmpty(), input);
            update(connection, "INSERT INTO catalog(name,category,price) VALUES(?,?,?)", input, null, new BigDecimal("1.25"));
            var rows = query(connection, "SELECT name,category,price FROM catalog WHERE name=?", input);
            check("引号文本及NULL原样存储", rows.size() == 1 && input.equals(rows.getFirst().get("name"))
                    && rows.getFirst().get("category") == null, rows);
            check("金额使用DECIMAL/BigDecimal", ((BigDecimal) rows.getFirst().get("price")).compareTo(new BigDecimal("1.25")) == 0, rows);
            check("IS NULL筛选", number(connection, "SELECT COUNT(*) FROM catalog WHERE category IS NULL") == 1, 1);
        }
        System.out.println("值使用占位符；表名、列名、排序方向不能靠值参数替代，应由固定SQL或允许列表控制。");
    }

    static void batch() throws Exception {
        System.out.println("--- 批处理：成功提交与失败后的整批回滚 ---");
        try (var db = new DatabaseSupport(); var connection = db.connection()) {
            connection.setAutoCommit(false);
            try {
                try (var batch = prepare(connection, "INSERT INTO catalog(name,price) VALUES(?,?)")) {
                    for (int i = 1; i <= 3; i++) { batch.setString(1, "item-" + i); batch.setInt(2, i); batch.addBatch(); }
                    int[] counts = batch.executeBatch();
                    check("成功批次返回三项结果", counts.length == 3 && Arrays.stream(counts).allMatch(n -> n == 1 || n == Statement.SUCCESS_NO_INFO), Arrays.toString(counts));
                }
                connection.commit();
                try (var batch = prepare(connection, "INSERT INTO catalog(name,price) VALUES(?,?)")) {
                    batch.setString(1, "valid-but-rolled-back"); batch.setInt(2, 1); batch.addBatch();
                    batch.setString(1, "invalid-negative-price"); batch.setInt(2, -1); batch.addBatch();
                    try { batch.executeBatch(); throw new AssertionError("Expected batch constraint failure"); }
                    catch (BatchUpdateException expected) {
                        System.out.println("batch failed SQLState=" + expected.getSQLState() + ", updateCounts=" + Arrays.toString(expected.getUpdateCounts()));
                        check("失败批次确为约束拒绝", expected.getSQLState() != null && expected.getSQLState().startsWith("23"), expected.getSQLState());
                        connection.rollback();
                    }
                }
                check("失败批次不留下已执行部分", number(connection, "SELECT COUNT(*) FROM catalog") == 3
                        && number(connection, "SELECT COUNT(*) FROM catalog WHERE name=?", "valid-but-rolled-back") == 0,
                        query(connection, "SELECT name,price FROM catalog ORDER BY id"));
            } catch (Exception | Error failure) { connection.rollback(); throw failure; }
            finally { connection.rollback(); }
        }
        System.out.println("批处理本身不等于原子事务；失败时可能已有部分语句执行，需要显式事务回滚。updateCounts细节由驱动决定。");
    }

    static void queries() throws Exception {
        System.out.println("--- 查询：JOIN、聚合、HAVING、NULL、窗口函数与稳定分页 ---");
        try (var db = new DatabaseSupport(); var connection = db.connection()) {
            update(connection, "INSERT INTO catalog(id,name,category,price) VALUES(1,'apple','food',3),(2,'book','media',10),(3,'rice','food',5)");
            update(connection, "INSERT INTO sales VALUES(1,1,2),(2,1,3),(3,2,1)");
            var totals = query(connection, "SELECT c.name,COALESCE(SUM(s.qty),0) AS units FROM catalog c LEFT JOIN sales s ON s.item_id=c.id GROUP BY c.id,c.name ORDER BY c.id");
            check("LEFT JOIN保留未售商品", totals.size() == 3 && ((Number) totals.get(0).get("units")).intValue() == 5
                    && ((Number) totals.get(2).get("units")).intValue() == 0, totals);
            var revenue = query(connection, "SELECT c.category,SUM(c.price*s.qty) AS revenue FROM catalog c JOIN sales s ON s.item_id=c.id GROUP BY c.category HAVING SUM(c.price*s.qty)>=? ORDER BY c.category", 12);
            check("GROUP BY/HAVING筛选收入", revenue.size() == 1 && revenue.getFirst().get("category").equals("food")
                    && ((BigDecimal) revenue.getFirst().get("revenue")).compareTo(new BigDecimal("15")) == 0, revenue);
            var ranked = query(connection, "SELECT name,ROW_NUMBER() OVER(ORDER BY price DESC,id) AS position FROM catalog ORDER BY position");
            check("窗口排名按价格降序", ranked.stream().map(row -> row.get("name")).toList().equals(List.of("book", "rice", "apple")), ranked);
            var offset = query(connection, "SELECT id FROM catalog ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", 1, 2);
            var keyset = query(connection, "SELECT id FROM catalog WHERE id>? ORDER BY id FETCH FIRST ? ROWS ONLY", 1, 2);
            check("固定数据上偏移分页与游标分页结果一致", offset.equals(keyset) && offset.size() == 2
                    && ((Number) offset.getFirst().get("id")).longValue() == 2, offset);
        }
        System.out.println("分页需要稳定且唯一的排序键；这里只验证固定数据集，不保证并发插入时两种分页都无重复/遗漏。");
    }

    static void constraints() throws Exception {
        System.out.println("--- 数据库约束：主键、唯一、非空、CHECK与外键 ---");
        try (var db = new DatabaseSupport(); var connection = db.connection()) {
            constraint(connection, "INSERT INTO accounts VALUES(1,'duplicate-id',1,0)");
            constraint(connection, "INSERT INTO accounts VALUES(3,'Alice',1,0)");
            constraint(connection, "INSERT INTO catalog(name,price) VALUES(?,?)", null, 1);
            constraint(connection, "UPDATE accounts SET balance=-1 WHERE id=1");
            constraint(connection, "INSERT INTO sales VALUES(1,999,1)");
            check("失败语句没有改变有效数据", number(connection, "SELECT COUNT(*) FROM accounts") == 2
                    && number(connection, "SELECT balance FROM accounts WHERE id=1") == 100
                    && number(connection, "SELECT COUNT(*) FROM sales") == 0, balances(connection));
        }
    }
}
