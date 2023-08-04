package cn.aulang.job.admin.datax.meta;

/**
 * Oracle语句构建器
 *
 * @author wulang
 */
public class OracleSqlBuilder extends BaseDatabaseSqlBuilder {

    private static volatile OracleSqlBuilder singleton;

    public static OracleSqlBuilder getInstance() {
        if (singleton == null) {
            synchronized (OracleSqlBuilder.class) {
                if (singleton == null) {
                    singleton = new OracleSqlBuilder();
                }
            }
        }
        return singleton;
    }

    @Override
    public String getTableNames(String dbName) {
        return "select table_name from user_tab_comments";
    }

    @Override
    public String getTableNameAndComment(String dbName) {
        return "select a.table_name,b.comments table_comment,a.num_rows table_rows "
                + "from user_tables a left join user_tab_comments b on a.table_name = b.table_name";
    }

    /**
     * is_nullable: Y/N
     * is_primary_key: P(主键)/U(唯一键)/R(外键)
     */
    @Override
    public String getColumnAndComment(String dbName, String tableName) {
        return "select a.table_name,a.column_name,a.data_type||'('||a.data_length||')' column_type,a.nullable is_nullable,"
                + "constraint_type is_primary_key,b.comments column_comment from user_tab_columns a "
                + "left join user_col_comments b on a.table_name = b.table_name and a.column_name = b.column_name "
                + "left join user_cons_columns c on a.table_name = c.table_name and a.column_name = c.column_name "
                + "left join user_constraints d on c.table_name = d.table_name and c.constraint_name = d.constraint_name "
                + "where a.table_name='" + tableName.toUpperCase() + "'";
    }

    @Override
    public String getDBColumnAndComment(String dbName) {
        return "select a.table_name,a.column_name,a.data_type||'('||a.data_length||')' column_type,a.nullable is_nullable,"
                + "constraint_type is_primary_key,b.comments column_comment from user_tab_columns a "
                + "left join user_col_comments b on a.table_name = b.table_name and a.column_name = b.column_name "
                + "left join user_cons_columns c on a.table_name = c.table_name and a.column_name = c.column_name "
                + "left join user_constraints d on c.table_name = d.table_name and c.constraint_name = d.constraint_name";
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

        return "select * from (select t.*, rownum rn from (" + sql + ") t where rn >= "
                + (page - 1) * size
                + " and rn < "
                + page * size;
    }

    @Override
    public Boolean isPrimaryKey(Object value) {
        return "P".equals(value);
    }

    @Override
    public Boolean isNullable(Object value) {
        return "Y".equals(value);
    }
}
