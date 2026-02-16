package cn.kuma.blog.framework.mybatisplus.pg.type.list;

/**
 * @author onozaty
 */
public class StringListTypeHandler extends ListTypeHandler<String> {

    public StringListTypeHandler() {
        super("text");
    }

}
