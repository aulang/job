package cn.aulang.job.admin.datax.meta;

/**
 * SQLServer语句构建器
 *
 * @author wulang
 */
public class SqlServerSqlBuilder extends BaseDatabaseSqlBuilder {

    private static volatile SqlServerSqlBuilder singleton;

    public static SqlServerSqlBuilder getInstance() {
        if (singleton == null) {
            synchronized (SqlServerSqlBuilder.class) {
                if (singleton == null) {
                    singleton = new SqlServerSqlBuilder();
                }
            }
        }
        return singleton;
    }

    @Override
    public String getTableNames(String dbName) {
        return "select name from sys.tables where type = 'U'";
    }

    @Override
    public String getTableNameAndComment(String dbName) {
        return "select a.name table_name,b.value column_comment,c.rows table_rows from sys.tables a "
                + "left join sys.extended_properties b on a.object_id = b.major_id and b.minor_id = 0 "
                + "left join sys.sysindexes c on a.object_id = c.id where a.type = 'U'";
    }

    /**
     * is_nullable: true/false
     * is_primary_key: true/false
     */
    @Override
    public String getColumnAndComment(String dbName, String tableName) {
        return "select a.name table_name,b.name column_name,c.name+'('+convert(varchar,b.max_length)+')' column_type,"
                + "b.is_nullable,e.is_primary_key,f.value column_comment from sys.tables a "
                + "inner join sys.columns b on a.object_id = b.object_id "
                + "left join sys.types c on b.system_type_id = c.system_type_id "
                + "left join sys.index_columns d on a.object_id = d.object_id and b.column_id = d.index_column_id "
                + "left join sys.indexes e on a.object_id = e.object_id and d.index_id = e.index_id "
                + "left join sys.extended_properties f on f.class=1 and a.object_id=f.major_id and b.column_id=f.minor_id "
                + "where a.type = 'U' and a.name = '" + tableName + "'";
    }

    @Override
    public String getDBColumnAndComment(String dbName) {
        return "select a.name table_name,b.name column_name,c.name+'('+convert(varchar,b.max_length)+')' column_type,"
                + "b.is_nullable,e.is_primary_key,f.value column_comment from sys.tables a "
                + "inner join sys.columns b on a.object_id = b.object_id "
                + "left join sys.types c on b.system_type_id = c.system_type_id "
                + "left join sys.index_columns d on a.object_id = d.object_id and b.column_id = d.index_column_id "
                + "left join sys.indexes e on a.object_id = e.object_id and d.index_id = e.index_id "
                + "left join sys.extended_properties f on f.class=1 and a.object_id=f.major_id and b.column_id=f.minor_id "
                + "where a.type = 'U'";
    }

    @Override
    public String getSelectTable(String tableName, String where, String sort, int page, int size, String... columnNames) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 1;
        }

        String sql = getSelectTable(tableName, where, sort, columnNames);

        sql = sql + " offset " + (page - 1) * size + " rows fetch next " + size + "rows only";

        return sql;
    }

    @Override
    public Boolean isPrimaryKey(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return "true".equals(value);
    }

    @Override
    public Boolean isNullable(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return "true".equals(value);
    }
}
