package cn.kuma.blog.framework.mybatisplus.pg.type.list;

/**
 * @author onozaty
 */
public class BooleanListTypeHandler extends ListTypeHandler<Boolean> {

    public BooleanListTypeHandler() {
        super("boolean");
    }

}
