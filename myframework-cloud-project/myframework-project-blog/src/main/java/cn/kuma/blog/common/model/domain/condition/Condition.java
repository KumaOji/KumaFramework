package cn.kuma.blog.common.model.domain.condition;

import cn.kuma.blog.common.util.SqlUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("all")
public class Condition {

    private List<Expressions> expressions;

    private HashMap<String, String> map;

    public String toSql() {
        map = new HashMap<>();
        if (this.getExpressions() == null || this.getExpressions().isEmpty()) {
            return "";
        }
        boolean hasCondition = false;
        StringBuilder sql = new StringBuilder();
        for (Expressions expression : this.getExpressions()) {
            String field = SqlUtils.escapeSql(expression.getField());
            if (CollectionUtils.isEmpty(expression.getValue())) {
                continue;
            }
            List<String> values = expression.getValue()
                .stream()
                .filter(StringUtils::isNoneBlank)
                .map((value) -> String.format("'%s'", SqlUtils.escapeSql(value)))
                .toList();
            if (CollectionUtils.isEmpty(values)) {
                continue;
            }
            String condition = switch (expression.getRelation()) {
                case IN -> String.format("%s IN (%s)", field, String.join(", ", values));
                case NOT_IN -> String.format("%s NOT IN (%s)", field, String.join(", ", values));
                case GT -> String.format("%s > %s", field, values.get(0));
                case LT -> String.format("%s < %s", field, values.get(0));
                case GE -> String.format("%s >= %s", field, values.get(0));
                case LE -> String.format("%s <= %s", field, values.get(0));
                case LIKE -> String.format("%s LIKE CONCAT('%%', %s, '%%')", field, values.get(0));
                case NOT_LIKE -> String.format("%s NOT LIKE CONCAT('%%', %s, '%%')", field, values.get(0));
            };
            map.put(field, condition);
            sql.append(condition).append(" AND ");
            hasCondition = true;
        }

        if (hasCondition) {
            sql.setLength(sql.length() - 5);
        }
        return sql.toString();
    }

}
