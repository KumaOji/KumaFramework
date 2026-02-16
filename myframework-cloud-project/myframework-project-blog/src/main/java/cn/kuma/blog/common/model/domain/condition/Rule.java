package cn.kuma.blog.common.model.domain.condition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Jleeci
 * @date 2024/5/23 15:28
 */
@Data
@Schema(name = "Rule", description = "复杂查询")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {

    @Schema(description = "匹配字段")
    private String field;

    @Schema(description = "操作符")
    private Relation relation;

    @JsonIgnore
    private String fieldType = "string";

    @Schema(description = "匹配值")
    private List<String> value;

    @Schema(description = "当验证失败时，错误信息")
    private String errorMessage;

}
