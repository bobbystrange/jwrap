package org.dreamcat.jwrap.jpa;

import static org.dreamcat.common.sql.ResultSetUtil.toInsertSql;
import static org.dreamcat.jwrap.jpa.util.SqlRowSetUtil.bacthGetAll;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.util.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2020/6/8
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JdbcExtender {

    static final String DEFAULT_PRIMARY_KEY = "id";
    final JdbcTemplate jdbcTemplate;

    public int load(String table, SqlRowSet rs, int batchNumber) throws SQLException {
        int columnSize = this.getColumnSize(table);

        int rowAffected = 0;
        int batchSize;
        List<Object[]> batchArgs;

        long timestamp;
        long interval = 0;
        while (true) {
            batchArgs = bacthGetAll(rs, columnSize, batchNumber);
            batchSize = batchArgs.size();
            rowAffected += batchSize;
            if (batchSize == 0) break;

            log.info("importing {}, current {} already {}", table, batchSize, rowAffected);
            timestamp = System.currentTimeMillis();
            jdbcTemplate.batchUpdate(String.format("insert into `%s` values (%s)",
                    table, StringUtil.interval("?", ",", columnSize)),
                    batchArgs);
            timestamp = System.currentTimeMillis() - timestamp;
            log.info("imported {} records of {}, cost {}ms", batchSize, table, timestamp);
            interval += timestamp;
        }
        log.info("imported {}, total {} records and cost {}ms", table, rowAffected, interval);
        return rowAffected;
    }

    // load a sql file
    public int load(String filename) throws IOException {
        int rowAffected = 0;
        int batchSize = 0;
        long timestamp;
        long interval = 0L;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String sql = line.trim();
                if (sql.isEmpty()) continue;
                if (sql.startsWith("--")) continue;

                log.info("importing {}, current {} already {}", filename, batchSize, rowAffected);
                timestamp = System.currentTimeMillis();
                batchSize = jdbcTemplate.update(sql);
                timestamp = System.currentTimeMillis() - timestamp;
                log.info("imported {} records from {}, cost {}ms", batchSize, filename, timestamp);
                rowAffected += batchSize;
                interval += timestamp;
            }
        }
        log.info("imported {}, total {} records and cost {}ms", filename, rowAffected, interval);
        return rowAffected;
    }

    // export RowSet to a sql file
    public int export(File file, String table, SqlRowSet rs, int batchNumber) {
        int columnSize = this.getColumnSize(table);

        int rowAffected = 0;
        int batchSize;
        List<Object[]> batchArgs;
        long timestamp;
        long interval = 0L;

        String columnNames = String.join(",", getAllColumnNames(table));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("--\n");
            bw.write(String.format("-- Dump data for table `%s`\n", table));
            bw.write("--\n");
            bw.flush();

            while (true) {
                batchArgs = bacthGetAll(rs, columnSize, batchNumber);
                batchSize = batchArgs.size();
                rowAffected += batchSize;
                if (batchSize == 0) break;

                log.info("exporting {}, current {} already {}", table, batchSize, rowAffected);
                timestamp = System.currentTimeMillis();
                String sql = toInsertSql(batchArgs, table, columnNames);
                bw.write(sql);
                bw.newLine();
                bw.flush();

                timestamp = System.currentTimeMillis() - timestamp;
                log.info("exported {} records of {}, cost {}ms", batchSize, table, timestamp);
                interval += timestamp;
            }

            log.info("exported {}, total {} records and cost {}ms", table, rowAffected, interval);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rowAffected;
    }

    public JdbcExtenderExport.Builder newExport() {
        return new JdbcExtenderExport.Builder(new JdbcExtenderExport(this));
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public Long primaryKeyMaximum(String table) {
        return primaryKeyMaximum(table, DEFAULT_PRIMARY_KEY);
    }

    public Long primaryKeyMaximum(String table, String primaryKeyName) {
        return jdbcTemplate.queryForObject("select ? from ? order by ? desc limit 1",
                Long.class, primaryKeyName, table, primaryKeyName);
    }

    public Long primaryKeyMinimum(String table) {
        return primaryKeyMinimum(table, DEFAULT_PRIMARY_KEY);
    }

    public Long primaryKeyMinimum(String table, String primaryKeyName) {
        return jdbcTemplate.queryForObject("select ? from ? order by ? asc limit 1",
                Long.class, primaryKeyName, table, primaryKeyName);
    }

    public boolean tableExists(String table) {
        if (table.indexOf('`') != -1) {
            throw new UnsupportedOperationException();
        }

        try {
            jdbcTemplate.execute(desc(table));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getColumnSize(String table) {
        if (table.indexOf('`') != -1) {
            throw new UnsupportedOperationException();
        }

        SqlRowSet columns = jdbcTemplate.queryForRowSet(desc(table));
        int columnSize = 0;
        while (columns.next()) {
            columnSize = columns.getRow();
        }
        return columnSize;
    }

    public List<String> getAllColumnNames(String table) {
        return jdbcTemplate.query(desc(table),
                (rs, rowNum) -> rs.getString(1));
    }

    private static String desc(String table) {
        return String.format("desc `%s`", table);
    }
}
