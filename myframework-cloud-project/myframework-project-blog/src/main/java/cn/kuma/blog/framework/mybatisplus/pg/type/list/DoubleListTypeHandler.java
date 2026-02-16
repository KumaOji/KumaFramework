package cn.kuma.blog.framework.mybatisplus.pg.type.list;

/**
 * @author onozaty
 */
public class DoubleListTypeHandler extends ListTypeHandler<Double> {

    public DoubleListTypeHandler() {
        super("float8");
    }

}
