package org.dreamcat.jwrap.jpa.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * Create by tuke on 2019-03-05
 */
public final class SqlRowSetUtil {

    private SqlRowSetUtil() {
    }

    public static Object[] getAll(SqlRowSet rs, int columnSize) {
        Object[] a = new Object[columnSize];
        for (int i = 0; i < columnSize; i++) {
            a[i] = rs.getObject(i + 1);
        }
        return a;
    }

    public static List<Object[]> bacthGetAll(SqlRowSet rs, int columnSize, int batchNumber) {
        List<Object[]> a = new ArrayList<>(batchNumber);
        for (int i = 0; i < batchNumber; i++) {
            if (!rs.next()) break;
            a.add(getAll(rs, columnSize));
        }
        return a;
    }

}
