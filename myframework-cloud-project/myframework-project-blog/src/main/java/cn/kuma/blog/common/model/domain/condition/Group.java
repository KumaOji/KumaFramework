package cn.kuma.blog.common.model.domain.condition;

import lombok.Data;

import java.util.List;

/**
 * @author Jleeci
 * @date 2024/5/23 15:27
 */
@Data
public class Group {

    private Operate operate;

    private boolean not;

    private List<Object> expressions;

}
