package cn.aulang.job.admin.datax.parser;

import cn.aulang.job.admin.datax.db.Column;
import cn.aulang.job.admin.datax.db.Table;
import cn.aulang.job.admin.datax.meta.DatabaseSqlBuilder;
import cn.aulang.job.admin.datax.meta.DatabaseSqlBuilderFactory;
import cn.aulang.job.admin.enums.DatabaseTypeEnum;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobDataSource;
import cn.aulang.job.admin.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽象数据库结构解析器
 *
 * @author wulang
 */
public class BaseStructureParser implements DatabaseStructureParser {

    protected static final Logger logger = LoggerFactory.getLogger(BaseStructureParser.class);

    private final DatabaseSqlBuilder sqlBuilder;
    private final SingleConnectionDataSource datasource;

    public BaseStructureParser(JobDataSource dataSource) {
        DatabaseTypeEnum databaseType = DatabaseTypeEnum.match(dataSource.getType());
        if (databaseType == null) {
            throw new JobException("Unsupported database: " + dataSource.getType());
        }
        sqlBuilder = DatabaseSqlBuilderFactory.getByDbType(databaseType);

        this.datasource = new SingleConnectionDataSource(
                dataSource.getJdbcUrl(),
                dataSource.getUsername(),
                dataSource.getPassword(),
                true);
        this.datasource.setDriverClassName(databaseType.getDriver());
    }

    @Override
    public boolean test() {
        try {
            DatabaseMetaData metaData = datasource.getConnection().getMetaData();
            return StringUtils.isNotBlank(metaData.getDatabaseProductName());
        } catch (SQLException e) {
            logger.warn("Test database connection fail", e);
            return false;
        }
    }

    @Override
    public List<Table> getTables() throws Exception {
        List<Table> tables = new ArrayList<>();

        Connection connection = datasource.getConnection();

        String dbName = connection.getSchema() != null ? connection.getSchema() : connection.getCatalog();

        String getTableSql = sqlBuilder.getTableNameAndComment(dbName);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(getTableSql)) {

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String comment = resultSet.getString(2);
                Long count = resultSet.getLong(3);
                tables.add(Table.of(name, comment, count));
            }
        }

        return tables;
    }

    @Override
    public List<String> getTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();

        Connection connection = datasource.getConnection();

        String dbName = connection.getSchema() != null ? connection.getSchema() : connection.getCatalog();

        String getTableNamesSql = sqlBuilder.getTableNames(dbName);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(getTableNamesSql)) {

            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                tableNames.add(tableName);
            }
        }

        return tableNames;
    }

    @Override
    public List<Column> getColumns(String tableName) throws SQLException {
        List<Column> columns = new ArrayList<>();

        Connection connection = datasource.getConnection();

        String dbName = connection.getSchema() != null ? connection.getSchema() : connection.getCatalog();

        String getColumnSql = sqlBuilder.getColumnAndComment(dbName, tableName);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(getColumnSql)) {

            while (resultSet.next()) {
                String table = resultSet.getString(1);
                String name = resultSet.getString(2);
                String type = resultSet.getString(3);
                Boolean isNullable = sqlBuilder.isNullable(resultSet.getObject(4));
                Boolean isPrimaryKey = sqlBuilder.isPrimaryKey(resultSet.getObject(5));
                String comment = resultSet.getString(6);

                Column column = new Column();
                column.setTable(table);
                column.setName(name);
                column.setType(type);
                column.setIsPrimaryKey(isPrimaryKey);
                column.setIsNullable(isNullable);
                column.setComment(comment);
                columns.add(column);
            }
        }

        return columns;
    }

    @Override
    public List<String> getColumnNames(String tableName) throws SQLException {
        List<String> columnNames = new ArrayList<>();

        Connection connection = datasource.getConnection();

        String dbName = connection.getSchema() != null ? connection.getSchema() : connection.getCatalog();

        String getTableNamesSql = sqlBuilder.getColumnNames(dbName, tableName);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(getTableNamesSql)) {

            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                columnNames.add(columnName);
            }
        }

        return columnNames;
    }

    private String toNoSelectDataSql(String sql) {
        String querySql = sql.trim();

        if (querySql.endsWith(";")) {
            int length = querySql.length();
            querySql = querySql.substring(0, length - 1);
        }

        int lastIndex = querySql.lastIndexOf(")");
        if (lastIndex > 0) {
            int firstIndex = querySql.indexOf("(");

            if (querySql.substring(0, firstIndex).contains("where")
                    || querySql.substring(lastIndex).contains("where")) {
                querySql = querySql + " and 1 = 0";
            } else {
                querySql = querySql + " where 1 = 0";
            }
        } else {
            if (querySql.contains("where")) {
                querySql = querySql + " and 1 = 0";
            } else {
                querySql = querySql + " where 1 = 0";
            }
        }

        return querySql;
    }

    @Override
    public List<Column> getSqlColumns(String sql) throws SQLException {
        String querySql = toNoSelectDataSql(sql);

        List<Column> columns = new ArrayList<>();

        try (Statement statement = datasource.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(querySql)) {

            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Column column = new Column();

                column.setTable(metaData.getTableName(i));
                column.setName(metaData.getColumnLabel(i));
                column.setType(metaData.getColumnTypeName(i));

                columns.add(column);
            }
        }

        return columns;
    }

    @Override
    public List<String> getSqlColumnNames(String sql) throws SQLException {
        String querySql = toNoSelectDataSql(sql);

        List<String> columns = new ArrayList<>();

        try (Statement statement = datasource.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(querySql)) {

            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnLabel(i));
            }
        }

        return columns;
    }

    @Override
    public Object getMaxValue(String tableName, String columnName) throws SQLException {
        String getMaxValueSql = sqlBuilder.getMaxValue(tableName, columnName);

        try (Statement statement = datasource.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(getMaxValueSql)) {

            if (resultSet.next()) {
                return resultSet.getObject(1);
            }

            return null;
        }
    }

    @Override
    public Pageable<Map<String, Object>> select(String tableName, String where, String sort, int page, int size, String... columns)
            throws SQLException {
        String countSql = sqlBuilder.getCountTable(tableName, where);

        long total = 0L;

        Connection connection = datasource.getConnection();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countSql)) {

            if (resultSet.next()) {
                total = resultSet.getLong(1);
            }
        }

        SimplePage<Map<String, Object>> pageable = new SimplePage<>(page, size);
        if (total == 0) {
            return pageable;
        }

        List<Map<String, Object>> list = new ArrayList<>();

        String getSelectTableSql = sqlBuilder.getSelectTable(tableName, where, sort, page, size, columns);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(getSelectTableSql)) {
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();

                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object columnValue = resultSet.getObject(columnName);
                    map.put(columnName, format(tableName, columnName, columnValue));
                }

                list.add(map);
            }
        }

        return pageable.setTotal(total).setList(list);
    }

    @Override
    public long insert(String tableName, Map<String, Object> row) throws Exception {
        return insertOrDelete(tableName, row, true);
    }

    @Override
    public long update(String tableName, Map<String, Object> row) throws SQLException {
        if (CollectionUtils.isEmpty(row)) {
            return 0;
        }

        List<Column> columns = getColumns(tableName);

        Map<String, Column> columnMap = columns
                .parallelStream()
                .collect(Collectors.toMap(Column::getName, Function.identity(), (k1, k2) -> k1));

        List<String> whereKeys = new ArrayList<>();
        List<String> valueKeys = new ArrayList<>();

        List<Object> values = new ArrayList<>();
        List<Object> primaries = new ArrayList<>();

        // 过滤空值，取到真正的表字段
        row.forEach((k, v) -> {
            if (v == null || (v instanceof String && StringUtils.isBlank(v.toString()))) {
                return;
            }

            if (!columnMap.containsKey(k)) {
                return;
            }

            Column column = columnMap.get(k);

            if (column.getIsPrimaryKey() != null && column.getIsPrimaryKey()) {
                whereKeys.add(k);
                primaries.add(v);
            } else {
                valueKeys.add(k);
                values.add(v);
            }
        });

        if (whereKeys.isEmpty()) {
            throw new JobException("No identity found");
        }

        if (valueKeys.isEmpty()) {
            throw new JobException("No update values found");
        }

        String updateRowSql = sqlBuilder.getUpdateRow(tableName, valueKeys, whereKeys);

        try (PreparedStatement statement = datasource.getConnection().prepareStatement(updateRowSql)) {
            int index = 1;

            for (Object value : values) {
                statement.setObject(index, value);
                ++index;
            }

            for (Object value : primaries) {
                statement.setObject(index, value);
                ++index;
            }

            return statement.executeUpdate();
        }
    }

    @Override
    public long delete(String tableName, Map<String, Object> where) throws SQLException {
        return insertOrDelete(tableName, where, false);
    }

    private long insertOrDelete(String tableName, Map<String, Object> row, boolean insert) throws SQLException {
        if (CollectionUtils.isEmpty(row)) {
            return 0;
        }

        Set<String> columnNames = new HashSet<>(getColumnNames(tableName));

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        // 过滤空值，取到真正的表字段
        row.forEach((k, v) -> {
            if (columnNames.contains(k)) {
                columns.add(k);
                values.add(v);
            }
        });


        String sql;
        if (insert) {
            sql = sqlBuilder.getInsertRow(tableName, columns);
        } else {
            sql = sqlBuilder.getDeleteRow(tableName, columns);
        }

        try (PreparedStatement statement = datasource.getConnection().prepareStatement(sql)) {
            int index = 1;

            for (Object value : values) {
                statement.setObject(index, value);
                ++index;
            }

            return statement.executeUpdate();
        }
    }

    @Override
    public long deleteById(String tableName, String id) throws SQLException {
        List<Column> columns = getColumns(tableName)
                .parallelStream()
                .filter(e -> e.getIsPrimaryKey() != null && e.getIsPrimaryKey())
                .collect(Collectors.toList());

        if (columns.isEmpty()) {
            throw new JobException("No identity found");
        }

        if (columns.size() > 1) {
            throw new JobException("Multi identity found");
        }

        String deleteRowSql = sqlBuilder.getDeleteRow(tableName, Collections.singletonList(columns.get(0).getName()));

        try (PreparedStatement statement = datasource.getConnection().prepareStatement(deleteRowSql)) {

            if (NumberUtils.isLong(id)) {
                statement.setLong(1, NumberUtils.parseLong(id));
            } else {
                statement.setString(1, id);
            }
            return statement.executeUpdate();
        }
    }

    @Override
    public void close() {
        datasource.destroy();
    }
}
