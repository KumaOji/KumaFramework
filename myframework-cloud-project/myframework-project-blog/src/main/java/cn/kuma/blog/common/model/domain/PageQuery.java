package cn.kuma.blog.common.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageQuery {

    private static final int DEFAULT_LIMIT_VALUE = 10;

    private static final int DEFAULT_OFFSET_VALUE = 0;

    private int limit = DEFAULT_LIMIT_VALUE;

    private int offset = DEFAULT_OFFSET_VALUE;

    @Schema(title = "排序规则")
    @Valid
    private List<PageParam.Sort> sorts = new ArrayList<>();

    @Schema(title = "排序元素载体")
    @Getter
    @Setter
    public static class Sort {

        @Schema(title = "排序字段", example = "id")
        @Pattern(regexp = PageableConstants.SORT_FILED_REGEX, message = "排序字段格式非法")
        private String field;

        @Schema(title = "是否正序排序", example = "false")
        private boolean asc;

    }

}
