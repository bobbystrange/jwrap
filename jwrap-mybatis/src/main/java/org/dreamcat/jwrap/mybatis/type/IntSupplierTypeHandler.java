package org.dreamcat.jwrap.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.IntSupplier;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * Create by tuke on 2020/7/17
 */
public class IntSupplierTypeHandler extends BaseTypeHandler<IntSupplier> {

    private final IntSupplier[] enums;

    public IntSupplierTypeHandler(Class<IntSupplier> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.enums = clazz.getEnumConstants();
        if (this.enums == null) {
            throw new IllegalArgumentException(clazz.getSimpleName() + " does not represent an enum type");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, IntSupplier parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getAsInt());
    }

    @Override
    public IntSupplier getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return parse(value);
    }

    @Override
    public IntSupplier getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return parse(value);
    }

    @Override
    public IntSupplier getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        int value = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return parse(value);
    }

    private IntSupplier parse(int value) {
        for (IntSupplier e : enums) {
            if (e.getAsInt() == value) {
                return e;
            }
        }
        return null;
    }
}
