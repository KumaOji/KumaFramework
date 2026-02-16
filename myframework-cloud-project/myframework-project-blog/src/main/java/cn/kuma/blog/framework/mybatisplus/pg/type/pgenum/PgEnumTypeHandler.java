package cn.kuma.blog.framework.mybatisplus.pg.type.pgenum;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * @author onozaty
 */
public class PgEnumTypeHandler<T extends Enum<T>> extends BaseTypeHandler<T> {

    private final Class<T> enumType;

    public PgEnumTypeHandler(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.name(), Types.OTHER);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toEnum(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toEnum(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    private T toEnum(String name) {
        return name == null ? null : Enum.valueOf(enumType, name);
    }

}
