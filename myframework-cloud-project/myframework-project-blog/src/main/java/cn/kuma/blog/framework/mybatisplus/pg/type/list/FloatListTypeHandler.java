package cn.kuma.blog.framework.mybatisplus.pg.type.list;

/**
 * @author onozaty
 */
public class FloatListTypeHandler extends ListTypeHandler<Float> {

    public FloatListTypeHandler() {
        super("float4");
    }

}
