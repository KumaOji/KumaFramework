package com.kuma.boot.data.mybatis.mybatisplus;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PageUtilsTest {
    @Test
    void convertedPageRetainsTotalAndDerivedPageCountIncludingEmptyPages() {
        var source = new Page<Integer>(2, 10, 25);
        source.setRecords(List.of(11, 12));
        var converted = PageUtils.convertPage(source, Object::toString);
        assertThat(converted.getTotal()).isEqualTo(25);
        assertThat(converted.getPages()).isEqualTo(3);
        assertThat(converted.getCurrent()).isEqualTo(2);
        assertThat(converted.getSize()).isEqualTo(10);
        assertThat(converted.getRecords()).containsExactly("11", "12");
        var result = PageUtils.toResult(source, Object::toString);
        assertThat(result.totalSize()).isEqualTo(25);
        assertThat(result.totalPage()).isEqualTo(3);
        assertThat(result.currentPage()).isEqualTo(2);
        source.setRecords(List.of());
        assertThat(PageUtils.convertPage(source, Object::toString).getTotal()).isEqualTo(25);
    }

    @Test
    void paginationDefaultsAndTrustedSortAliasesDoNotAssumeATableColumn() {
        var query = new PageQuery();
        assertThat(MpUtils.buildMpPage(query).getCurrent()).isEqualTo(1);
        assertThat(MpUtils.buildMpPage(query).getSize()).isEqualTo(20);
        assertThat(MpUtils.buildMpPage(query).orders()).isEmpty();
        query.setSort("createdAt");
        query.setOrder("DESC");
        var page = MpUtils.buildMpPage(query, Map.of("createdAt", "create_date"));
        assertThat(page.orders().getFirst().getColumn()).isEqualTo("create_date");
        assertThat(page.orders().getFirst().isAsc()).isFalse();
        query.setSort("id; DROP TABLE orders");
        assertThatThrownBy(() -> MpUtils.buildMpPage(query)).isInstanceOf(IllegalArgumentException.class);
        query.setSort("unknown");
        assertThatThrownBy(() -> MpUtils.buildMpPage(query, Map.of("createdAt", "create_date")))
                .isInstanceOf(IllegalArgumentException.class);
        query.setCurrentPage(0);
        assertThatThrownBy(() -> MpUtils.buildMpPage(query)).isInstanceOf(IllegalArgumentException.class);
    }
}
