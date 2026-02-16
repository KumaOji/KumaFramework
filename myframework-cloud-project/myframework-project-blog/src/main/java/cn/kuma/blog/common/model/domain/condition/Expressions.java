package cn.kuma.blog.common.model.domain.condition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Expressions {

    private String field;

    private Relation relation;

    private List<String> value;

}
