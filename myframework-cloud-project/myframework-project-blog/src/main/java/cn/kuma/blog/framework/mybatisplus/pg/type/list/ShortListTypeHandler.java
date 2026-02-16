package cn.kuma.blog.framework.mybatisplus.pg.type.list;

/**
 * @author onozaty
 */
public class ShortListTypeHandler extends ListTypeHandler<Short> {

    public ShortListTypeHandler() {
        super("int2");
    }

}
