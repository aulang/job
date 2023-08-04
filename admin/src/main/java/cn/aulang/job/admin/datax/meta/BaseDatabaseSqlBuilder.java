package cn.aulang.job.admin.datax.meta;

import cn.aulang.job.core.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 数据库SQL语句构建器基类
 *
 * @author wulang
 */
public class BaseDatabaseSqlBuilder implements DatabaseSqlBuilder {

    private static volatile BaseDatabaseSqlBuilder singleton;

    public static BaseDatabaseSqlBuilder getInstance() {
        if (singleton == null) {
            synchronized (BaseDatabaseSqlBuilder.class) {
                if (singleton == null) {
                    singleton = new BaseDatabaseSqlBuilder();
                }
            }
        }
        return singleton;
    }

    @Override
    public String getTableNames(String dbName) {
        return "select table_name from information_schema.tables where table_type='BASE TABLE' "
                + "and table_schema = '" + dbName + "'";
    }

    @Override
    public String getTableNameAndComment(String dbName) {
        return "select table_name,table_comment,table_rows from information_schema.tables where table_type='BASE TABLE' "
                + "and table_schema = '" + dbName + "'";
    }

    @Override
    public String getColumnNames(String dbName, String tableName) {
        return "select * from " + tableName + " where 1 = 0";
    }

    /**
     * is_nullable: YES/NO
     * is_primary_key: PRI/UNI
     */
    @Override
    public String getColumnAndComment(String dbName, String tableName) {
        return "select table_name,column_name,column_type,is_nullable,column_key is_primary_key,column_comment "
                + "from information_schema.columns where table_schema = '" + dbName + "' and table_name = '" + tableName + "'";
    }

    @Override
    public String getDBColumnAndComment(String dbName) {
        return "select table_name,column_name,column_type,is_nullable,column_key is_primary_key,column_comment "
                + "from information_schema.columns where table_schema = '" + dbName + "'";
    }

    @Override
    public String getMaxValue(String tableName, String columnName) {
        return "select max(" + columnName + ") from " + tableName;
    }

    protected String getSelectTable(String tableName, String where, String sort, String... columnNames) {
        String columns = "*";
        if (columnNames != null && columnNames.length > 0) {
            columns = StringUtils.join(columnNames, Constants.SPLIT_COMMA);
        }

        String sql = "select " + columns + " from " + tableName;

        sql = appendWhere(sql, where);

        if (StringUtils.isNotBlank(sort)) {
            sql = sql + " order by " + sort;
        }

        return sql;
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

        sql = sql + " limit " + (page - 1) * size + "," + size;

        return sql;
    }

    @Override
    public String getCountTable(String tableName, String where) {
        String sql = "select count(*) from " + tableName;
        return appendWhere(sql, where);
    }

    @Override
    public String getInsertRow(String tableName, List<String> valueColumns) {
        StringBuilder builder = new StringBuilder("insert into " + tableName + "(");
        StringBuilder valueBuilder = new StringBuilder(" values (");


        int index = 0;

        for (String column : valueColumns) {
            if (index == 0) {
                builder.append(column);
                valueBuilder.append("?");
            } else {
                builder.append(",").append(column);
                valueBuilder.append(",").append("?");
            }
            ++index;
        }

        builder.append(")");
        valueBuilder.append(")");

        builder.append(valueBuilder);

        return builder.toString();
    }

    @Override
    public String getUpdateRow(String tableName, List<String> valueColumns, List<String> whereColumns) {
        StringBuilder builder = new StringBuilder("update " + tableName + " set ");

        int index = 0;

        for (String column : valueColumns) {
            if (index == 0) {
                builder.append(column).append(" = ? ");
            } else {
                builder.append(", ").append(column).append(" = ? ");
            }
            ++index;
        }

        index = 0;

        for (String column : whereColumns) {
            if (index == 0) {
                builder.append("where ").append(column).append(" = ? ");
            } else {
                builder.append(" and ").append(column).append(" = ? ");
            }
            ++index;
        }

        return builder.toString();
    }

    @Override
    public String getDeleteRow(String tableName, List<String> columns) {
        StringBuilder builder = new StringBuilder("delete from " + tableName + " where ");

        int index = 0;

        for (String column : columns) {
            if (index == 0) {
                builder.append(column).append(" = ? ");
            } else {
                builder.append(" and ").append(column).append(" = ? ");
            }
            ++index;
        }

        return builder.toString();
    }

    @Override
    public String appendWhere(String sql, String where) {
        if (StringUtils.isBlank(where)) {
            return sql;
        }

        String whereSql = where.trim();

        if (whereSql.startsWith("where")) {
            whereSql = whereSql.substring(5);
        }

        String querySql = sql.trim();

        if (querySql.endsWith(";")) {
            int length = querySql.length();
            querySql = querySql.substring(0, length - 1);
        }

        int last = querySql.lastIndexOf(")");
        if (last > 0) {
            int first = querySql.indexOf("(");

            if (querySql.substring(0, first).contains("where")
                    || querySql.substring(last + 1).contains("where")) {
                querySql = querySql + " and " + whereSql;
            } else {
                querySql = querySql + " where " + whereSql;
            }
        } else {
            if (querySql.contains("where")) {
                querySql = querySql + " and " + whereSql;
            } else {
                querySql = querySql + " where " + whereSql;
            }
        }

        return querySql;
    }

    @Override
    public Boolean isPrimaryKey(Object value) {
        return "PRI".equals(value);
    }

    @Override
    public Boolean isNullable(Object value) {
        return "YES".equals(value);
    }
}
