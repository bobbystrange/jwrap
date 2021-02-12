package org.dreamcat.jwrap.jpa;

import static org.dreamcat.jwrap.jpa.util.SqlRowSetUtil.bacthGetAll;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.core.Pair;
import org.dreamcat.common.function.ThrowableBiConsumer;
import org.dreamcat.common.sql.ResultSetUtil;
import org.dreamcat.common.x.jackson.JacksonUtil;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * Create by tuke on 2020/6/8
 */
@Slf4j
public class JdbcExtenderExport {

    private final JdbcExtender extender;
    // required
    private String table;
    private List<String> columnNamesArray = null;
    private String conditionString = "";
    private String primaryKeyName = JdbcExtender.DEFAULT_PRIMARY_KEY;
    private Pair<Long, Long> primaryKeyRange = null;
    private long recordNumber = Long.MAX_VALUE;
    // required
    private Integer batchNumber;
    // required
    private Integer splitNumber;
    private BiFunction<Long, Long, File> filenameProvider;

    JdbcExtenderExport(JdbcExtender extender) {
        this.extender = extender;
    }

    public int exportToCsv() {
        return export((bw, batchArgs) -> {
            for (Object[] batchArg : batchArgs) {
                String record = Arrays.stream(batchArg).map(arg -> {
                    if (arg == null) return "NULL";
                    else if (arg instanceof Boolean) {
                        return (boolean) arg ? "1" : "0";
                    } else {
                        return String.format("'%s'", escape(arg.toString()));
                    }
                }).collect(Collectors.joining("\t"));
                bw.write(record);
                bw.newLine();
            }
            bw.flush();
        });
    }

    public int exportToJsons() {
        List<String> columnNames;
        int columnSize;
        if (columnNamesArray == null) {
            columnSize = extender.getColumnSize(table);
            columnNames = extender.getAllColumnNames(table);
        } else {
            columnSize = columnNamesArray.size();
            columnNames = columnNamesArray;
        }

        return export((bw, batchArgs) -> {
            for (Object[] batchArg : batchArgs) {
                Map<String, Object> record = new HashMap<>();
                for (int i = 0; i < columnSize; i++) {
                    record.put(columnNames.get(i), batchArg[i]);
                }
                bw.write(Objects.requireNonNull(JacksonUtil.toJson(record)));
                bw.newLine();
            }
            bw.flush();
        });
    }

    // all data of table
    public int export() {
        String columnNames;
        if (columnNamesArray == null) {
            columnNames = String.join(",", extender.getAllColumnNames(table));
        } else {
            columnNames = String.join(",", columnNamesArray);
        }

        return export((bw, batchArgs) -> {
            String insertSql = ResultSetUtil.toInsertSql(batchArgs, table, columnNames);
            bw.write(insertSql);
            bw.newLine();
            bw.flush();
        });
    }

    public int export(ThrowableBiConsumer<BufferedWriter, List<Object[]>> outputAction) {
        Pair<Long, Long> minValueAndMaxValue = getMinValueAndMaxValue();
        long minValue = minValueAndMaxValue.first();
        long maxValue = minValueAndMaxValue.second();

        Pair<Integer, String> columnSizeAndSql = getColumnSizeAndSql();
        int columnSize = columnSizeAndSql.first();
        String sql = columnSizeAndSql.second();

        int rowAffected = 0;
        int batchSize;
        List<Object[]> batchArgs;
        long timestamp;
        long interval = 0;
        long offset = minValue;
        int size = batchNumber * splitNumber;
        while (true) {
            File file = filenameProvider.apply(offset, offset + size - 1);
            boolean hasMore = true;
            boolean enough = false;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("--\n");
                bw.write(String.format("-- Dump data for table `%s`\n", table));
                bw.write("--\n");
                bw.flush();

                for (int i = 0; i < splitNumber; i++) {
                    String selectSql = String.format(sql, offset, offset + batchNumber - 1);
                    log.info("performing sql: {}", selectSql);
                    SqlRowSet rs = extender.jdbcTemplate.queryForRowSet(selectSql);

                    batchArgs = bacthGetAll(rs, columnSize, batchNumber);
                    batchSize = batchArgs.size();

                    offset += batchNumber;
                    if (offset >= maxValue) {
                        hasMore = false;
                        break;
                    }
                    if (batchSize == 0) continue;

                    log.info("exporting {}, current {} already {}", table, batchSize, rowAffected);
                    rowAffected += batchSize;
                    if (rowAffected >= recordNumber) {
                        log.info("already get {} records from table `{}`", recordNumber, table);
                        enough = true;
                        break;
                    }

                    timestamp = System.currentTimeMillis();
                    outputAction.accept(bw, batchArgs);
                    timestamp = System.currentTimeMillis() - timestamp;
                    log.info("exported {} records of {}, cost {}ms", batchSize, table, timestamp);
                    interval += timestamp;
                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // no data any more, or already got enough data
            if (!hasMore || enough) break;
        }

        log.info("exported {}, total {} records and cost {}ms", table, rowAffected, interval);
        return rowAffected;
    }

    private Pair<Integer, String> getColumnSizeAndSql() {
        int columnSize;
        String columnNames;
        if (columnNamesArray == null) {
            columnSize = extender.getColumnSize(table);
            columnNames = String.join(",", extender.getAllColumnNames(table));
        } else {
            columnSize = columnNamesArray.size();
            columnNames = String.join(",", columnNamesArray);
        }

        String sql = String.format("select %s from `%s` where `%s` between %%d and %%d",
                columnNames, table, primaryKeyName);
        if (conditionString.length() > 0) {
            sql += " and " + conditionString;
        }
        return Pair.of(columnSize, sql);
    }

    private Pair<Long, Long> getMinValueAndMaxValue() {
        long minValue;
        long maxValue;
        if (primaryKeyRange != null) {
            minValue = primaryKeyRange.first();
            maxValue = primaryKeyRange.second();
        } else {
            Long primaryKeyMinimumValue = extender.primaryKeyMinimum(table, primaryKeyName);
            if (primaryKeyMinimumValue == null) {
                throw new RuntimeException(
                        String.format("cannot get minimum primary key `%s` in table `%s`",
                                primaryKeyName, table));
            }

            minValue = primaryKeyMinimumValue;
            Long primaryKeyMaximumValue = extender.primaryKeyMaximum(table, primaryKeyName);
            if (primaryKeyMaximumValue == null) {
                throw new RuntimeException(
                        String.format("cannot get maximum primary key `%s` in table `%s`",
                                primaryKeyName, table));
            }
            maxValue = primaryKeyMaximumValue;
        }
        if (maxValue < minValue || minValue < 0) {
            throw new IllegalArgumentException(
                    String.format("expect maxValue %d > minValue %d >= 0", maxValue, minValue));
        }
        return Pair.of(minValue, maxValue);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static class Builder {

        private final JdbcExtenderExport exporter;

        Builder(JdbcExtenderExport exporter) {
            this.exporter = exporter;
        }

        public JdbcExtenderExport build() {
            if (exporter.table == null) {
                throw new IllegalStateException("table is required");
            }
            if (exporter.batchNumber == null) {
                throw new IllegalStateException("batchNumber is required");
            }
            if (exporter.splitNumber == null) {
                throw new IllegalStateException("splitNumber is required");
            }

            if (exporter.filenameProvider == null) {
                exporter.filenameProvider = (i, j) ->
                        new File(String.format("%s-%d-%d.sql", exporter.table, i, j));
            }

            return exporter;
        }

        public Builder table(String table) {
            exporter.table = table;
            return this;
        }

        public Builder columnNamesArray(List<String> columnNamesArray) {
            exporter.columnNamesArray = columnNamesArray;
            return this;
        }

        public Builder conditionString(String conditionString) {
            exporter.conditionString = conditionString;
            return this;
        }

        public Builder primaryKeyName(String primaryKeyName) {
            exporter.primaryKeyName = primaryKeyName;
            return this;
        }

        public Builder primaryKeyRange(Pair<Long, Long> primaryKeyRange) {
            exporter.primaryKeyRange = primaryKeyRange;
            return this;
        }

        public Builder recordNumber(long recordNumber) {
            exporter.recordNumber = recordNumber;
            return this;
        }

        public Builder batchNumber(int batchNumber) {
            exporter.batchNumber = batchNumber;
            return this;
        }

        public Builder splitNumber(int splitNumber) {
            exporter.splitNumber = splitNumber;
            return this;
        }

        public Builder filenameProvider(BiFunction<Long, Long, File> filenameProvider) {
            exporter.filenameProvider = filenameProvider;
            return this;
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("'", "\\'");
    }

}
