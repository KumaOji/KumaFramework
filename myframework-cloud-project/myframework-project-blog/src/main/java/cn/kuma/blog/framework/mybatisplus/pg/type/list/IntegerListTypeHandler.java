package cn.kuma.blog.framework.mybatisplus.pg.type.list;

/**
 * @author onozaty
 */
public class IntegerListTypeHandler extends ListTypeHandler<Integer> {

    public IntegerListTypeHandler() {
        super("int4");
    }

}
