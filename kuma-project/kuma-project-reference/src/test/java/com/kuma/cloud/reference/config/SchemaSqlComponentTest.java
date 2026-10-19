package com.kuma.cloud.reference.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.kuma.boot.data.mybatis.mybatisplus.ReplaceSql;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaSqlComponentTest {
    @Test
    void schemaRewritePreservesPostgresCastAndReturningSyntax() {
        var select = ReplaceSql.replaceSql(DbType.POSTGRE_SQL, "kuma_reference_main",
                "SELECT id::text FROM purchase_orders WHERE id = ?");
        assertThat(select).contains("kuma_reference_main.purchase_orders").contains("::text");
        var insert = ReplaceSql.replaceSql(DbType.POSTGRE_SQL, "kuma_reference_main",
                "INSERT INTO purchase_orders (id) VALUES (?) RETURNING id");
        assertThat(insert).contains("kuma_reference_main.purchase_orders").containsIgnoringCase("RETURNING id");
    }
}
