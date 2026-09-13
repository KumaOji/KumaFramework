package com.kuma.cloud.lab.mysql.support;

import com.kuma.cloud.lab.mysql.domain.vo.MysqlTopicVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MysqlSyntaxCatalogTest {

    @Test
    void topicsCoverCreateAlterGroupAndWindow() {
        List<MysqlTopicVO> topics = MysqlSyntaxCatalog.topics();
        assertThat(topics)
                .extracting(MysqlTopicVO::id)
                .containsExactly("create-table", "add-column", "select", "group-by", "window");
        assertThat(topics)
                .allSatisfy(topic -> {
                    assertThat(topic.title()).isNotBlank();
                    assertThat(topic.syntax()).isNotBlank();
                    assertThat(topic.note()).isNotBlank();
                });
        assertThat(topics)
                .filteredOn(topic -> "window".equals(topic.id()))
                .singleElement()
                .extracting(MysqlTopicVO::syntax)
                .asString()
                .contains("PARTITION BY", "ROW_NUMBER", "RANK");
    }
}
