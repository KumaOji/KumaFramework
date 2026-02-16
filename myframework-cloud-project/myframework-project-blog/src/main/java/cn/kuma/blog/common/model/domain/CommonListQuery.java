package cn.kuma.blog.common.model.domain;

import cn.kuma.blog.common.model.domain.condition.Condition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonListQuery extends PageQuery {

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    private LocalDateTime endTime;

    private Condition condition;

}
