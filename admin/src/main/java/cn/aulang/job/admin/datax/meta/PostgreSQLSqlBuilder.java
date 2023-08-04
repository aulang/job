package cn.aulang.job.admin.datax.meta;

/**
 * PostgreSQL语句构建器
 *
 * @author wulang
 */
public class PostgreSQLSqlBuilder extends BaseDatabaseSqlBuilder {

    private static volatile PostgreSQLSqlBuilder singleton;

    public static PostgreSQLSqlBuilder getInstance() {
        if (singleton == null) {
            synchronized (PostgreSQLSqlBuilder.class) {
                if (singleton == null) {
                    singleton = new PostgreSQLSqlBuilder();
                }
            }
        }
        return singleton;
    }

    @Override
    public String getTableNames(String dbName) {
        return "select tablename from pg_tables where tablename not like 'pg%' and tablename not like 'sql_%' "
                + "and tablename not like 'deferred_%' order by tablename";
    }

    @Override
    public String getTableNameAndComment(String dbName) {
        return "select a.relname table_name,cast(obj_description(a.relfilenode,'pg_class') as varchar) table_comment,"
                + "b.n_live_tup table_rows from pg_class a left join pg_stat_user_tables b on a.relname = b.relname "
                + "where a.relkind = 'r' and a.relname not like 'pg_%' and a.relname not like 'sql_%' and a.relname not like 'deferred_%' "
                + "order by a.relname";
    }

    /**
     * is_nullable: true/false
     * is_primary_key: p
     */
    @Override
    public String getColumnAndComment(String dbName, String tableName) {
        return "select a.relname table_name,b.attname column_name,format_type(b.atttypid,b.atttypmod) column_type,"
                + "b.attnotnull is_nullable,c.contype is_primary_key,col_description(b.attrelid,b.attnum) column_comment from pg_class a "
                + "left join pg_attribute b on a.oid = b.attrelid and b.attnum > 0 "
                + "left join pg_constraint c on a.oid = c.conrelid and b.attnum = c.conkey[1] and c.contype = 'p' "
                + "where a.relkind = 'r' and a.relname = '" + tableName + "'";
    }

    @Override
    public String getDBColumnAndComment(String dbName) {
        return "select a.relname table_name,b.attname column_name,format_type(b.atttypid,b.atttypmod) column_type,"
                + "b.attnotnull is_nullable,c.contype is_primary_key,col_description(b.attrelid,b.attnum) column_comment from pg_class a "
                + "left join pg_attribute b on a.oid = b.attrelid and b.attnum > 0 "
                + "left join pg_constraint c on a.oid = c.conrelid and b.attnum = c.conkey[1] and c.contype = 'p' "
                + "where a.relkind = 'r' and a.relname not like 'pg_%' and a.relname not like 'sql_%' and a.relname not like 'deferred_%'";
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

        sql = sql + " limit " + size + " offset " + (page - 1) * size;

        return sql;
    }

    @Override
    public Boolean isPrimaryKey(Object value) {
        return "p".equals(value);
    }

    @Override
    public Boolean isNullable(Object value) {
        if (value instanceof Boolean) {
            return !(Boolean) value;
        }

        return !"true".equals(value);
    }
}
